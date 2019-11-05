package com.tpv.android.network.resources

import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.orZero


interface PaginatedStatefulResource<T,F> {

    /**
     * This method return if the resource is in it's end state. All the events had occurred
     * and there are no other events left to follow for them.
     *
     * @return true/false based if the current state is end state or not
     */
    fun isEndState(): Boolean

    fun isLastPage(): Boolean

    fun isLoading(): Boolean

    var nextPage: Int

}

class PaginatedResource<T , F > private constructor() : PaginatedStatefulResource<T,F> {
    override var nextPage: Int = 1
    var state: Int? = null
        private set
    var data: ArrayList<T> = ArrayList()
    var message: String? = null
    var throwable: Throwable? = null
    var paginatedMataData: PaginatedMataData? = null

    companion object {
        const val STATE_LOADING = 1
        const val STATE_SUCCESS = 2
        const val STATE_ERROR = 3
        const val STATE_LOADING_MORE = 4

        /* fun <T : Any> from(resource: PaginatedResource<T>?): PaginatedResource<T> {
             val paginatedResource = PaginatedResource<T>()
             paginatedResource.state = resource?.state
             paginatedResource.data = resource?.data
             paginatedResource.message = resource?.message
             paginatedResource.throwable = resource?.throwable
             paginatedResource.paginatedMataData = resource?.paginatedMataData
             return paginatedResource
         }*/

        fun <T : Any, F : Any> getInstance() = PaginatedResource<T,F>()
    }

    fun success(data: List<T>?, paginatedMataData: PaginatedMataData?): PaginatedResource<T,F> {
        this.data.addAll(data.orEmpty())
        this.state = STATE_SUCCESS
        this.paginatedMataData = paginatedMataData
        this.nextPage = paginatedMataData?.currentPage.orZero() + 1
        return this
    }

    fun failure(message: String?, throwable: Throwable?): PaginatedResource<T,F> {
        this.message = message; this.throwable = throwable; state = STATE_ERROR;return this
    }

    fun loading(data: List<T>? = null): PaginatedResource<T,F> {
        this.data.addAll(data.orEmpty())
        this.state =
                if (this.data.isEmpty()) STATE_LOADING else STATE_LOADING_MORE; return this
    }


    override fun isEndState(): Boolean =
            (state == STATE_ERROR) or (state == STATE_SUCCESS)

    override fun isLastPage(): Boolean = !paginatedMataData?.hasNext.orFalse()

    override fun isLoading(): Boolean =
            (state == STATE_LOADING) or (state == STATE_LOADING_MORE)
}