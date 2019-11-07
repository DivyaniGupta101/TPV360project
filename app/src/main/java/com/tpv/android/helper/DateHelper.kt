package com.tpv.android.helper

import java.text.SimpleDateFormat
import java.util.*

fun Date.formatDate(outputFormat: String): String = SimpleDateFormat(outputFormat, Locale.US).format(this)
