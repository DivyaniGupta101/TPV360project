package com.tpv.android.network.resources

fun <T> Resource<T>.isEndState() = (state == Resource.State.ERROR) or (state == Resource.State.SUCCESS)

fun <T> Resource<T>.ifSuccess(func: (data: T?) -> Unit) {
    if (state == Resource.State.SUCCESS) {
        func.invoke(this.data)
    }
}


fun <T> Resource<T>.ifFailure(func: (message: String?) -> Unit) {
    if (state == Resource.State.ERROR) {
        func.invoke(this.message.orEmpty())
    }
}