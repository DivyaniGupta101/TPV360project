package com.tpv.android.helper

import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.orZero
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError

/**
 * Sets the page change listener and provides the callback for page change
 * @param callback Provide a callback with page number
 * @return Instance of page change listener
 */
fun <T> RecyclerView.setPagination(
        liveData: LiveData<Resource<T, APIError>>,
        callback: (page: Int) -> Unit
): OnPageChangeListener {

    var mPageNo = 1

    // initially load data
    callback(mPageNo)

    val listener: OnPageChangeListener = object : OnPageChangeListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // check if pagination is enabled and current state is not loading
            if (paginationEnabled && !(liveData.value?.isLoading().orFalse()) && liveData.value?.hasNextPage.orFalse()) {
                // check for load more
                val layoutManager = recyclerView.layoutManager
                if (layoutManager is LinearLayoutManager) {
                    val lastItem = layoutManager.findLastVisibleItemPosition()
                    if (lastItem >= recyclerView.adapter?.itemCount.orZero() - 2) {
                        mPageNo++
                        callback(mPageNo)
                    }
                }
            }
        }

        override fun resetPage(toPage: Int) {
            mPageNo = toPage
        }
    }
    addOnScrollListener(listener)
    return listener
}

/**
 * Class used by the add on page change listener
 */
abstract class OnPageChangeListener : RecyclerView.OnScrollListener() {


    /**
     * States that pagination is enabled or not
     * Useful when there is no more data and can be set as false to disable it
     */
    internal var paginationEnabled = true

    /**
     * Resets the current page to provided page number.
     * Default is 1
     * @param toPage page no you want to change page to (optional) (default: 1)
     */
    abstract fun resetPage(toPage: Int = 1)

}