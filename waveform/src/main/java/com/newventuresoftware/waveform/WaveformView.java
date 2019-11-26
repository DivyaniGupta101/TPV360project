package com.newventuresoftware.waveform;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.newventuresoftware.waveform.utils.AudioUtils;
import com.newventuresoftware.waveform.utils.SamplingUtils;
import com.newventuresoftware.waveform.utils.TextUtils;

import java.util.LinkedList;

/**
 * TODO: document your custom view class.
 */
public class WaveformView extends View {

    private static final String TAG = "WaveformView";

    public static final int MODE_RECORDING = 1;
    public static final int MODE_PLAYBACK = 2;

    private TextPaint mTextPaint;
    private Paint mStrokePaint, mFillPaint, mMarkerPaint;

    // Used in draw
    private int brightness;
    private Rect drawRect;

    private int width, height;
    private float xStep, centerY;
    private int mMode, mAudioLength, mMarkerPosition, mSampleRate, mChannels;
    private short[] mSamples;
    private LinkedList<Short> mSampleList;
    private LinkedList<float[]> mHistoricalData;
    private Picture mCachedWaveform;
    private Bitmap mCachedWaveformBitmap;
    private int colorDelta = 255 /*/ (historySize + 1)*/;
    private boolean showTextAxis = true;

    private float speed;
    private float barPerSec;
    private float barGravity;
    private long lastDraw;

    public WaveformView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public WaveformView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.WaveformView, defStyle, 0);

        mMode = a.getInt(R.styleable.WaveformView_mode, MODE_PLAYBACK);

        speed = a.getFloat(R.styleable.WaveformView_speed, 1f);
        barPerSec = a.getFloat(R.styleable.WaveformView_barPerSec, 1f);
        barGravity = a.getFloat(R.styleable.WaveformView_gravity, 1f);
        float strokeThickness = a.getFloat(R.styleable.WaveformView_waveformStrokeThickness, 1f);
        int mStrokeColor = a.getColor(R.styleable.WaveformView_waveformColor,
                ContextCompat.getColor(context, R.color.default_waveform));
        int mFillColor = a.getColor(R.styleable.WaveformView_waveformFillColor,
                ContextCompat.getColor(context, R.color.default_waveformFill));
        int mMarkerColor = a.getColor(R.styleable.WaveformView_playbackIndicatorColor,
                ContextCompat.getColor(context, R.color.default_playback_indicator));
        int mTextColor = a.getColor(R.styleable.WaveformView_timecodeColor,
                ContextCompat.getColor(context, R.color.default_timecode));

        a.recycle();

        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(TextUtils.getFontSize(getContext(),
                android.R.attr.textAppearanceSmall));

        mStrokePaint = new Paint();
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setStrokeWidth(strokeThickness);
        mStrokePaint.setAntiAlias(true);

        mFillPaint = new Paint();
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(mFillColor);

        mMarkerPaint = new Paint();
        mMarkerPaint.setStyle(Paint.Style.STROKE);
        mMarkerPaint.setStrokeWidth(0);
        mMarkerPaint.setAntiAlias(true);
        mMarkerPaint.setColor(mMarkerColor);

        lastDraw = System.currentTimeMillis();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        width = getMeasuredWidth();
        height = getMeasuredHeight();
        xStep = width / (mAudioLength * 1.0f);
        centerY = height / 2f;
        drawRect = new Rect(0, 0, width, height);

        if (mHistoricalData != null) {
            mHistoricalData.clear();
        }
        if (mMode == MODE_PLAYBACK) {
            createPlaybackWaveform();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        LinkedList<float[]> temp = mHistoricalData;
        if (mMode == MODE_RECORDING && temp != null) {
            for (float[] p : temp) {
                canvas.drawLine(p[0], centerY - (p[1] - centerY), p[0], centerY + (p[1] - centerY), mStrokePaint);
            }
        } else if (mMode == MODE_PLAYBACK) {
            if (mCachedWaveform != null) {
                canvas.drawPicture(mCachedWaveform);
            } else if (mCachedWaveformBitmap != null) {
                canvas.drawBitmap(mCachedWaveformBitmap, null, drawRect, null);
            }
            if (mMarkerPosition > -1 && mMarkerPosition < mAudioLength)
                canvas.drawLine(xStep * mMarkerPosition, 0, xStep * mMarkerPosition, height, mMarkerPaint);
        }
    }

    public void reset() {
        if (mSampleList != null) mSampleList.clear();
        if (mHistoricalData != null) mHistoricalData.clear();
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mMode) {
        mMode = mMode;
    }

    public short[] getSamples() {
        return mSamples;
    }

    public void setSamples(short[] samples) {
        mSamples = samples;
        calculateAudioLength();
        onSamplesChanged();
    }

    public int getMarkerPosition() {
        return mMarkerPosition;
    }

    public void setMarkerPosition(int markerPosition) {
        mMarkerPosition = markerPosition;
        postInvalidate();
    }

    public int getAudioLength() {
        return mAudioLength;
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    public void setSampleRate(int sampleRate) {
        mSampleRate = sampleRate;
        calculateAudioLength();
    }

    public int getChannels() {
        return mChannels;
    }

    public void setChannels(int channels) {
        mChannels = channels;
        calculateAudioLength();
    }

    public boolean showTextAxis() {
        return showTextAxis;
    }

    public void setShowTextAxis(boolean showTextAxis) {
        this.showTextAxis = showTextAxis;
    }

    private void calculateAudioLength() {
        if (mSamples == null || mSampleRate == 0 || mChannels == 0)
            return;

        mAudioLength = AudioUtils.calculateAudioLength(mSamples.length, mSampleRate, mChannels);
    }

    private void onSamplesChanged() {
        if (mMode == MODE_RECORDING) {
            if (mHistoricalData == null) {
                lastDraw = System.currentTimeMillis();
                mHistoricalData = new LinkedList<>();
            }

            if (mSampleList == null)
                mSampleList = new LinkedList<>();

            LinkedList<float[]> temp = new LinkedList<>(mHistoricalData);

            // For efficiency, we are reusing the array of points.
            float[] waveformPoints;
            if (temp.size() > 5 && temp.getFirst()[0] < 0) {
                waveformPoints = temp.removeFirst();
            } else {
                waveformPoints = new float[width * 4];
            }

            long now = System.currentTimeMillis();
            if (now - lastDraw > 1000 / barPerSec) {
                drawRecordingWaveform(mSampleList.toArray(new Short[]{}), waveformPoints);
                temp.addLast(waveformPoints);
                mHistoricalData = temp;
                mSampleList.clear();
                lastDraw = now;
            } else {
                for (short mSample : mSamples) {
                    mSampleList.add(mSample);
                }
            }

            for (float[] t : mHistoricalData) {
                t[0] -= speed;
            }

            postInvalidate();

        } else if (mMode == MODE_PLAYBACK) {
            mMarkerPosition = -1;
            xStep = width / (mAudioLength * 1.0f);
            createPlaybackWaveform();
        }
    }

    void drawRecordingWaveform(Short[] buffer, float[] waveformPoints) {

        try {
            float max = Short.MAX_VALUE;
            float maxY = 0f;

            for (int x = 0; x < width; x++) {
                int index = (int) (((x * 1.0f) / width) * buffer.length);
                short sample = buffer[index];
                float y = centerY - (((sample / barGravity) / max) * centerY);

                maxY = Math.max(maxY, y);
            }

            waveformPoints[0] = width;
            waveformPoints[1] = maxY;
        } catch (Exception e) {

        }
    }

    Path drawPlaybackWaveform(int width, int height, short[] buffer) {
        Path waveformPath = new Path();
        float centerY = height / 2f;
        float max = Short.MAX_VALUE;

        short[][] extremes = SamplingUtils.getExtremes(buffer, width);


        waveformPath.moveTo(0, centerY);

        // draw maximums
        for (int x = 0; x < width; x++) {
            short sample = extremes[x][0];
            float y = centerY - ((sample / max) * centerY);
            waveformPath.lineTo(x, y);
        }

        // draw minimums
        for (int x = width - 1; x >= 0; x--) {
            short sample = extremes[x][1];
            float y = centerY - ((sample / max) * centerY);
            waveformPath.lineTo(x, y);
        }

        waveformPath.close();
        Log.e(TAG, "drawPlaybackWaveform: " + System.currentTimeMillis());
        return waveformPath;
    }

    private void createPlaybackWaveform() {
        if (width <= 0 || height <= 0 || mSamples == null)
            return;

        Canvas cacheCanvas;
        if (Build.VERSION.SDK_INT >= 23 && isHardwareAccelerated()) {
            mCachedWaveform = new Picture();
            cacheCanvas = mCachedWaveform.beginRecording(width, height);
        } else {
            mCachedWaveformBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            cacheCanvas = new Canvas(mCachedWaveformBitmap);
        }

        Path mWaveform = drawPlaybackWaveform(width, height, mSamples);
        cacheCanvas.drawPath(mWaveform, mFillPaint);
        cacheCanvas.drawPath(mWaveform, mStrokePaint);
        drawAxis(cacheCanvas, width);

        if (mCachedWaveform != null)
            mCachedWaveform.endRecording();
    }

    private void drawAxis(Canvas canvas, int width) {
        if (!showTextAxis) return;
        int seconds = mAudioLength / 1000;
        float xStep = width / (mAudioLength / 1000f);
        float textHeight = mTextPaint.getTextSize();
        float textWidth = mTextPaint.measureText("10.00");
        int secondStep = (int) (textWidth * seconds * 2) / width;
        secondStep = Math.max(secondStep, 1);
        for (float i = 0; i <= seconds; i += secondStep) {
            canvas.drawText(String.format("%.2f", i), i * xStep, textHeight, mTextPaint);
        }
    }
}
