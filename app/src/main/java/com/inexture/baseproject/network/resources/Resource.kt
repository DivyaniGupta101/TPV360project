package com.inexture.baseproject.network.resources


class Resource<out T> private constructor(
    val state: State = State.NONE,
    val data: T?,
    val message: String? = null
) {
    companion object {
        /**
         * Creates an empty resource with no state
         * This can be useful while a request is yet to be made to network
         */
        fun <T> empty() = Resource<T>(data = null)

        /**
         * Creates a new resource with the loading state and the given data
         * @param data Excepts the data of the given type T (Optional)
         * @return Resource object with state loading
         */
        fun <T> loading(data: T? = null) = Resource<T>(state = State.LOADING, data = data)

        /**
         * Creates the new resource with the success state and given data
         * @param data Excepts the data of the given type T
         * @return Resource object with state success
         */
        fun <T> success(data: T?) = Resource<T>(state = State.SUCCESS, data = data)

        /**
         * Creates the new resource with the error state and message
         * You can optionally pass the data and throwable too
         * @param data Excepts the data of the given type T (Optional)
         * @param throwable The error object (Optional)
         * @return Resource object with state error
         */
        fun <T> error(data: T? = null, message: String? = null) =
            Resource(state = State.ERROR, data = data, message = message)
    }

    enum class State { NONE, LOADING, LOADING_MORE, SUCCESS, ERROR }
}

