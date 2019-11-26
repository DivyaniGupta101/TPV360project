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

package com.tpv.android.utils.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Message
import com.livinglifetechway.k4kotlin.core.orZero

import java.io.File
import java.io.IOException

class RecordingThread
/**
 * Default constructor. Setup recorder with default sampling rate 1 channel,
 * 16 bits pcm
 */(private val mRecordFile: File, private val mListener: AudioDataReceivedListener) {
    private var mAudioRecord: AudioRecord? = null
    private var mBufferSize: Int = 0
    private var mPCMBuffer: ShortArray? = null
    private var mEncodeThread: DataEncodeThread? = null
    var isRecording = false
        private set

    var volume: Int = 0
        private set

    val maxVolume: Int
        get() = MAX_VOLUME

    /**
     * Start recording. Create an encoding thread. Start record from this
     * thread.
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun start() {
        if (isRecording)
            return
        initAudioRecorder()
        mAudioRecord?.startRecording()
        object : Thread() {

            override fun run() {
                // 设置线程权限
                android.os.Process
                        .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
                isRecording = true
                while (isRecording) {
                    val readSize = mAudioRecord?.read(mPCMBuffer!!, 0, mBufferSize).orZero()
                    if (readSize > 0) {
                        mPCMBuffer?.let { mEncodeThread?.addTask(it, readSize) }
                        calculateRealVolume(mPCMBuffer, readSize)

                        mListener.onAudioDataReceived(mPCMBuffer)
                    }
                }
                // release and finalize audioRecord
                mAudioRecord?.stop()
                mAudioRecord?.release()
                mAudioRecord = null
                // stop the encoding thread and try to wait
                // until the thread finishes its job
                val msg = Message.obtain(mEncodeThread?.handler,
                        DataEncodeThread.PROCESS_STOP)
                msg.sendToTarget()
            }

            /**
             * 此计算方法来自samsung开发范例
             *
             * @param buffer
             * buffer
             * @param readSize
             * readSize
             */
            private fun calculateRealVolume(buffer: ShortArray?, readSize: Int) {
                var sum = 0
                for (i in 0 until readSize) {
                    // 这里没有做运算的优化，为了更加清晰的展示代码
                    sum += buffer?.get(i).orZero() * buffer?.get(i).orZero()
                }
                if (readSize > 0) {
                    val amplitude = (sum / readSize).toDouble()
                    volume = Math.sqrt(amplitude).toInt()
                }
            }
        }.start()
    }

    fun stop() {
        isRecording = false
    }

    /**
     * Initialize audio recorder
     */
    @Throws(IOException::class)
    private fun initAudioRecorder() {
        mBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLING_RATE,
                DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.audioFormat)

        val bytesPerFrame = DEFAULT_AUDIO_FORMAT.bytesPerFrame
        /*
		 * Get number of samples. Calculate the buffer size (round up to the
		 * factor of given frame size) 使能被整除，方便下面的周期性通知
		 */
        var frameSize = mBufferSize / bytesPerFrame
        if (frameSize % FRAME_COUNT != 0) {
            frameSize += FRAME_COUNT - frameSize % FRAME_COUNT
            mBufferSize = frameSize * bytesPerFrame
        }

        /* Setup audio recorder */
        mAudioRecord = AudioRecord(DEFAULT_AUDIO_SOURCE,
                DEFAULT_SAMPLING_RATE, DEFAULT_CHANNEL_CONFIG,
                DEFAULT_AUDIO_FORMAT.audioFormat, mBufferSize)

        mPCMBuffer = ShortArray(mBufferSize)
        /*
		 * Initialize lame buffer mp3 sampling rate is the same as the recorded
		 * pcm sampling rate The bit rate is 32kbps
		 */
//        MP3Encoder.init(DEFAULT_SAMPLING_RATE, DEFAULT_LAME_IN_CHANNEL,
//                DEFAULT_SAMPLING_RATE, DEFAULT_LAME_MP3_BIT_RATE,
//                DEFAULT_LAME_MP3_QUALITY)
        // Create and run thread used to encode data
        // The thread will
        mEncodeThread = DataEncodeThread(mRecordFile, mBufferSize)
        mEncodeThread?.start()
        mAudioRecord?.setRecordPositionUpdateListener(mEncodeThread,
                mEncodeThread?.handler)
        mAudioRecord?.positionNotificationPeriod = FRAME_COUNT
    }

    companion object {
        // =======================AudioRecord Default
        // Settings=======================
        private val DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
        /**
         * 以下三项为默认配置参数。Google Android文档明确表明只有以下3个参数是可以在所有设备上保证支持的。
         */
        val DEFAULT_SAMPLING_RATE = 44100// 模拟器仅支持从麦克风输入8kHz采样率
        private val DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        /**
         * 下面是对此的封装 private static final int DEFAULT_AUDIO_FORMAT =
         * AudioFormat.ENCODING_PCM_16BIT;
         */
        private val DEFAULT_AUDIO_FORMAT = PCMFormat.PCM_16BIT

        // ======================Lame Default Settings=====================
        val DEFAULT_LAME_MP3_QUALITY = 4
        /**
         * 与DEFAULT_CHANNEL_CONFIG相关，因为是mono单声，所以是1
         */
        val DEFAULT_LAME_IN_CHANNEL = 1
        /**
         * Encoded bit rate. MP3 file will be encoded with bit rate 32kbps
         */
        val DEFAULT_LAME_MP3_BIT_RATE = 32

        // ==================================================================

        /**
         * 自定义 每160帧作为一个周期，通知一下需要进行编码
         */
        private val FRAME_COUNT = 160

        private val MAX_VOLUME = 2000
    }
}