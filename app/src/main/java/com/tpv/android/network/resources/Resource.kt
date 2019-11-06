package com.tpv.android.network.resources


class Resource<T, F> private constructor(
        var state: State = State.NONE,
        var data: T? = null,
        var exception: Throwable? = null,
        var errorData: F? = null,
        var hasNextPage: Boolean = true // for pagination
) {

    fun isLoading() =
        (state == State.LOADING) or (state == State.LOADING_MORE)


    companion object {
        /**
         * Creates an empty resource with no state
         * This can be useful while a request is yet to be made to network
         */
        fun <T, F> empty() = Resource<T, F>()

        /**
         * Creates a new resource with the loading state and the given data
         * @param data Excepts the data of the given type T (Optional)
         * @return Resource object with state loading
         */
        fun <T, F> loading(data: T? = null) = Resource<T, F>(state = State.LOADING, data = data)

        /**
         * Creates a new resource with the loading state and the given data
         * @param data Excepts the data of the given type T (Optional)
         * @return Resource object with state loading more
         */
        fun <T, F> loadingMore(data: T?) = Resource<T, F>(state = State.LOADING_MORE, data = data)

        /**
         * Creates the new resource with the success state and given data
         * @param data Excepts the data of the given type T
         * @return Resource object with state success
         */
        fun <T, F> success(data: T?) = Resource<T, F>(state = State.SUCCESS, data = data)

        /**
         * Creates the new resource with the error state and message
         * You can optionally pass the data and throwable too
         * @param data Excepts the data of the given type T (Optional)
         * @param throwable The error object (Optional)
         * @return Resource object with state error
         */
        fun <T, F> error(data: T? = null, throwable: Throwable? = null, errorData: F? = null) =
            Resource(state = State.ERROR, data = data, exception = throwable, errorData = errorData)

    }

    enum class State { NONE, LOADING, SUCCESS, ERROR, LOADING_MORE }
}

