/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tpv.android.utils.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.naman14.androidlame.AndroidLame;
import com.naman14.androidlame.LameBuilder;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class RecordingThread {
    private static final String TAG = "RecordingThread";
    private static final String LOG_TAG = RecordingThread.class.getSimpleName();
    private static final int SAMPLE_RATE = 8000;

    public RecordingThread(String filePath, AudioDataReceivedListener listener) {
        this.filePath = filePath;
        mListener = listener;
    }

    private boolean mShouldContinue;
    private String filePath;
    private AudioDataReceivedListener mListener;
    private Thread mThread;
    private BufferedOutputStream os = null;

    public boolean recording() {
        return mThread != null;
    }

    public void startRecording() {
        if (mThread != null)
            return;

        try {
            os = new BufferedOutputStream(new FileOutputStream(filePath));

        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found for recording ", e);
        }

        mShouldContinue = true;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                record();
            }
        });
        mThread.start();
    }

    public void stopRecording() {
        if (mThread == null)
            return;

        mShouldContinue = false;
        mThread = null;
    }

    private void record() {
        Log.v(LOG_TAG, "Start");
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // buffer size in bytes
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        short[] audioBuffer = new short[bufferSize];
        LinkedList<short[]> audioBufferList = new LinkedList<>();
        LinkedList<Integer> bufferSizeList = new LinkedList<>();

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!");
            return;
        }
        record.startRecording();

        Log.v(LOG_TAG, "Start recording");


        AndroidLame androidLame = new LameBuilder()
                .setInSampleRate(SAMPLE_RATE)
                .setOutChannels(1)
                .setOutBitrate(32)
                .setOutSampleRate(SAMPLE_RATE)
                .build();


        long shortsRead = 0;
        while (mShouldContinue) {
            int numberOfShort = record.read(audioBuffer, 0, bufferSize);
            shortsRead += numberOfShort;

            audioBufferList.add(audioBuffer);
            bufferSizeList.add(numberOfShort);

            // Notify waveform
            mListener.onAudioDataReceived(audioBuffer);
        }


        byte[] byteAudio = new byte[(int) (7200 + bufferSize * 2.5)];


        for (int i = 0; i < audioBufferList.size(); i++) {
            int numberOfShort = bufferSizeList.get(i);
            short[] b = audioBufferList.get(i);
            try {
                int bytesEncoded = androidLame.encode(b, b, numberOfShort, byteAudio);
                os.write(byteAudio, 0, bytesEncoded);
            } catch (IOException e) {
                Log.e(TAG, "Error saving recording ", e);
                return;
            }
        }


        try {
            int outputMp3buf = androidLame.flush(byteAudio);
            os.write(byteAudio, 0, outputMp3buf);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        record.stop();
        record.release();


        Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
    }
}
