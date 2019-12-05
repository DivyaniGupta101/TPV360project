package com.tpv.android.network.resources

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

data class Success<T, F> internal constructor(val data: T?, val message: String? = "") : Result<T, F>()
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