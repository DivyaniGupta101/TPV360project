package com.tpv.android.utils.audio

import android.media.AudioFormat

enum class PCMFormat private constructor(val bytesPerFrame: Int, val audioFormat: Int) {
    PCM_8BIT(1, AudioFormat.ENCODING_PCM_8BIT),
    PCM_16BIT(2, AudioFormat.ENCODING_PCM_16BIT)
}