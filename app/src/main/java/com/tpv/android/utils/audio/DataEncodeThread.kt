package com.tpv.android.utils.audio

import android.media.AudioRecord
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.livinglifetechway.k4kotlin.core.orZero
import com.naman14.androidlame.AndroidLame
import com.naman14.androidlame.LameBuilder

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.CountDownLatch

class DataEncodeThread
/**
 * Constructor
 * @param file file
 * @param bufferSize bufferSize
 * @throws FileNotFoundException
 */
@Throws(FileNotFoundException::class)
constructor(file: File, bufferSize: Int) : Thread(), AudioRecord.OnRecordPositionUpdateListener {
    private var mHandler: StopHandler? = null
    private val mMp3Buffer: ByteArray
    private val mFileOutputStream: FileOutputStream?

    private val mHandlerInitLatch = CountDownLatch(1)

    private val androidLame: AndroidLame = LameBuilder()
            .setInSampleRate(RecordingThread.DEFAULT_SAMPLING_RATE)
            .setOutChannels(RecordingThread.DEFAULT_LAME_IN_CHANNEL)
            .setOutBitrate(RecordingThread.DEFAULT_LAME_MP3_BIT_RATE)
            .setOutSampleRate(RecordingThread.DEFAULT_SAMPLING_RATE)
            .setQuality(RecordingThread.DEFAULT_LAME_MP3_QUALITY)
            .build()

    /**
     * Return the handler attach to this thread
     */
    val handler: Handler?
        get() {
            try {
                mHandlerInitLatch.await()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return mHandler
        }
    private val mTasks = Collections.synchronizedList(ArrayList<Task>())

    /**
     * @see <a>https://groups.google.com/forum/?fromgroups=.!msg/android-developers/1aPZXZG6kWk/lIYDavGYn5UJ</a>
     *
     * @author buihong_ha
     */
    internal class StopHandler(encodeThread: DataEncodeThread) : Handler() {

        var encodeThread: WeakReference<DataEncodeThread>

        init {
            this.encodeThread = WeakReference(encodeThread)
        }

        override fun handleMessage(msg: Message) {
            if (msg.what == PROCESS_STOP) {
                val threadRef = encodeThread.get()
                //处理缓冲区中的数据
                while (threadRef?.processData().orZero() > 0);
                // Cancel any event left in the queue
                removeCallbacksAndMessages(null)
                threadRef?.flushAndRelease()
                looper.quit()
            }
            super.handleMessage(msg)
        }
    }

    init {
        this.mFileOutputStream = FileOutputStream(file)
        mMp3Buffer = ByteArray((7200 + bufferSize.toDouble() * 2.0 * 1.25).toInt())
    }

    override fun run() {
        Looper.prepare()
        mHandler = StopHandler(this)
        mHandlerInitLatch.countDown()
        Looper.loop()
    }

    override fun onMarkerReached(recorder: AudioRecord) {
        // Do nothing
    }

    override fun onPeriodicNotification(recorder: AudioRecord) {
        processData()
    }

    /**
     * 从缓冲区中读取并处理数据，使用lame编码MP3
     * @return  从缓冲区中读取的数据的长度
     * 缓冲区中没有数据时返回0
     */
    private fun processData(): Int {
        if (mTasks.size > 0) {
            val task = mTasks.removeAt(0)
            val buffer = task.data
            val readSize = task.readSize
            val encodedSize = androidLame.encode(buffer, buffer, readSize, mMp3Buffer)
            if (encodedSize > 0) {
                try {
                    mFileOutputStream?.write(mMp3Buffer, 0, encodedSize)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return readSize
        }
        return 0
    }

    /**
     * Flush all data left in lame buffer to file
     */
    private fun flushAndRelease() {
        //将MP3结尾信息写入buffer中
        val flushResult = androidLame.flush(mMp3Buffer)
        if (flushResult > 0) {
            try {
                mFileOutputStream?.write(mMp3Buffer, 0, flushResult)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (mFileOutputStream != null) {
                    try {
                        mFileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                androidLame.close()
            }
        }
    }

    fun addTask(rawData: ShortArray, readSize: Int) {
        mTasks.add(Task(rawData, readSize))
    }

    private inner class Task(rawData: ShortArray, val readSize: Int) {
        val data: ShortArray

        init {
            this.data = rawData.clone()
        }
    }

    companion object {
        val PROCESS_STOP = 1
    }
}