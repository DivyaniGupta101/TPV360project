package com.tpv.android.network.resources

import androidx.lifecycle.MutableLiveData
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.orZero
import com.tpv.android.model.PaginateCommonResp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class Result<T, F> {
    companion object {
        fun <T, F> success(data: T?) = Success<T, F>(data)
        fun <T, F> failure(error: F?) = Failure<T, F>(null, error)
        fun <T, F> failure(throwable: Throwable?) = Failure<T, F>(throwable, null)
        fun <T, F> failure(throwable: Throwable?, error: F?) = Failure<T, F>(throwable, error)


        fun <T, F> map(func: () -> T): Result<T, F> = try {
            success(func())
        } catch (e: Exception) {
            failure(e)
        }

        suspend fun <T, F> suspendableMap(func: suspend () -> T): Result<T, F> = try {
            success(func())
        } catch (e: Exception) {
            failure(e)
        }

        /**
         * Checks if given all the results are successful or not
         * @return boolean representing all results are successful or not
         */
        fun allSuccess(vararg results: Result<*, *>) = results.all { result -> result is Success }

        /**
         * Get the list of failures among the given results
         * @return List of Results which are failure
         */
        fun getFailures(vararg results: Result<*, *>) =
                results.filter { result -> result is Failure }
    }
}

data class Success<T, F> internal constructor(val data: T?, val message: String? = "", val paginatedMataData: PaginatedMataData? = null) : Result<T, F>()
data class Failure<T, F> internal constructor(val throwable: Throwable?, val error: F?) :
        Result<T, F>()

/**
 * This function executes the passed lambda if the result is of success type
 * The return type is result, the same object it is is invoked on, so that you can
 * chain the onFailure function on it to get the results you want
 * And can also save the value in a variable after chaining
 */
fun <T, F> Result<T, F>.onSuccess(func: (T?) -> Unit): Result<T, F> {
    if (this is Success) func(data)
    return this
}

/**
 * This function executes the passed lambda if the result is of failure type
 * The return type is result, the same object it is is invoked on, so that you can
 * chain the onSuccess function on it to get the results you want
 * And can also save the value in a variable after chaining
 */
fun <T, F> Result<T, F>.onFailure(func: (Throwable?, F?) -> Unit): Result<T, F> {
    if (this is Failure) func(throwable, error)
    return this
}

val <T, F> Result<T, F>.isSuccess: Boolean get() = this is Success
val <T, F> Result<T, F>.isFailure: Boolean get() = this is Failure

/**
 * Converts the given result into another type
 */
fun <T, F, R> Result<T, F>.map(transform: (T?) -> R): Result<R, F> = try {
    when (this) {
        is Success -> Result.success(transform(this.data))
        is Failure -> Result.failure(this.throwable, this.error)
    }
} catch (e: Exception) {
    Result.failure(e)
}

/**
 * Converts the given result into another type
 */
suspend fun <T, F, R> Result<T, F>.suspendableMap(transform: suspend (T?) -> R): Result<R, F> =
        try {
            when (this) {
                is Success -> Result.success(transform(this.data))
                is Failure -> Result.failure(this.throwable, this.error)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }


class PaginatedMataData(var currentPage: Int?, // 1
                        var lastPage: Int?, // 1
                        var perPage: Int?, // 10
                        var total: Int? // 6
) {
    constructor(paginatedMataData: PaginatedMataData?) : this(
            paginatedMataData?.currentPage,
            paginatedMataData?.lastPage,
            paginatedMataData?.perPage,
            paginatedMataData?.total
    )

    var hasNext: Boolean = false
        get() = lastPage.orZero() > currentPage.orZero()
    var hasPrevious: Boolean = false
        get() = 1 < currentPage.orZero()

    companion object {
        const val PAGE_SIZE = 10
    }
}


fun <T : Any, F : Any> CoroutineScope.paginatedDataCall(
        exposeLiveData: MutableLiveData<PaginatedResource<T, F>>?,
        func: suspend (page: Int) -> Result<PaginateCommonResp<List<T>>, F>
): MutableLiveData<PaginatedResource<T, F>> {
    val liveData = exposeLiveData ?: MutableLiveData()
    if (liveData.value?.isLoading().orFalse() || liveData.value?.isLastPage().orFalse()) {
        return liveData
    }
    liveData.value = (liveData.value ?: PaginatedResource.getInstance()).loading()
    launch {
        val resource = liveData.value ?: PaginatedResource.getInstance()
        liveData.value = resource.loading()
        val result = func.invoke(liveData.value?.nextPage.orZero())
        if (result is Success) {
            liveData.value = resource.success(result.data?.data, result.paginatedMataData)
        }
        if (result is Failure) {
            liveData.value = resource.failure(result.error.toString(), result.throwable)
        }
    }

    return liveData
}

//fun PaginatedResource<Any>.ifSuccess(func: (PaginatedResource<Any>) -> Unit) {
//    if (state == PaginatedResource.STATE_SUCCESS) {
//        func.invoke(this)
//    }
//}


//fun <T , J > Result<T, J>.map(function: (T?) -> J?): Result<T, J> {
//    return when (this) {
//        is Success -> {
//            success(function.invoke(this.data), this.message, this.paginatedMataData)
//        }
//        is Failure ->
//            failure(this.message, this.error)
//    }
//}
