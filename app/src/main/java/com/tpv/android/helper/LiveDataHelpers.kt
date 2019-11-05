package com.tpv.android.helper

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.asLiveData() = this