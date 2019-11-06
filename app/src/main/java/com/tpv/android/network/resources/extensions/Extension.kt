package com.tpv.android.network.resources.extensions

import com.tpv.android.network.resources.Resource

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


//fun <T, F> PaginatedResource<T, F>.ifSuccess(func: (data: List<T>?, paginatedMataData: PaginatedMataData?) -> Unit) {
//    if (state == PaginatedResource.STATE_SUCCESS) {
//        func.invoke(this.data, this.paginatedMataData)
//    }
//}
//

//fun <T, F> PaginatedResource<T, F>.ifFailure(func: (message: String?, throwable: Throwable?) -> Unit) {
//    if (state == PaginatedResource.STATE_ERROR) {
//        func.invoke(this.message, this.throwable)
//    }
//}
