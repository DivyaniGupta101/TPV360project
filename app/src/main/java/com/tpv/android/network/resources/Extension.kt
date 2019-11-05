package com.tpv.android.network.resources

/**
 * return true if the network call is in completed state
 * * @return boolean
 */
fun <T, F> Resource<T, F>.isEndState() = (state == Resource.State.ERROR) or (state == Resource.State.SUCCESS)

fun <T, F> Resource<T, F>.ifSuccess(func: (data: T?) -> Unit) {
    if (state == Resource.State.SUCCESS) {
        func.invoke(this.data)
    }
}


fun <T, F> Resource<T, F>.ifFailure(func: (throwable: Throwable?, errorData: F?) -> Unit) {
    if (state == Resource.State.ERROR) {
        func.invoke(this.exception, this.errorData)
    }
}