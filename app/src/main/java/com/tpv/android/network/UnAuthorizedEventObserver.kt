package com.tpv.android.network

object UnAuthorizedEventObserver {
    private val mListeners: MutableList<() -> Unit> = mutableListOf()

    fun observe(func: () -> Unit) {
        mListeners.add(func)
    }

    fun notifyObservers() {
        mListeners.forEach { it.invoke() }
    }
}