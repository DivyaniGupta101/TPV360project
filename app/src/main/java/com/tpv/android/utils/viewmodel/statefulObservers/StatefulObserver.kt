package com.tpv.android.utils.viewmodel.statefulObservers

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.livinglifetechway.k4kotlin.core.orFalse
//
//interface StatefulResource<T> {
//
//    /**
//     * This method return if the resource is in it's end state. All the events had occurred
//     * and there are no other events left to follow for them.
//     *
//     * @return true/false based if the current state is end state or not
//     */
//    fun isEndState(): Boolean
//
//}
//
//class Resource<T : Any> private constructor() : StatefulResource<T> {
//
//    var state: Int? = null
//        private set
//    var data: T? = null
//    var message: String? = null
//    var throwable: Throwable? = null
//
//    companion object {
//        const val STATE_LOADING = 1
//        const val STATE_SUCCESS = 2
//        const val STATE_ERROR = 3
//
//        fun <T : Any> success(data: T?) =
//                Resource<T>().apply { this.data = data; state = STATE_SUCCESS }
//
//        fun <T : Any> failure(message: String?, throwable: Throwable?) =
//                Resource<T>().apply { this.message = message; this.throwable = throwable; state = STATE_ERROR; }
//
//        fun <T : Any> loading() =
//                Resource<T>().apply { this.state = STATE_LOADING }
//
//
//    }
//
//    fun updateState(state: Int?, message: String?, throwable: Throwable?): Resource<T> {
//        state
//                ?: return this;this.state = state; this.message = message; this.throwable = throwable; return this
//    }
//
//    override fun isEndState() = (state == STATE_ERROR) or (state == STATE_SUCCESS)
//
//}
//
//fun <J : StatefulResource<T>, T> LiveData<J>.observeStatefully(
//        lifecycleOwner: LifecycleOwner,
//        observer: Observer<J>
//) {
//    val liveData = this
//    val internalObserver = object : Observer<J> {
//        override fun onChanged(resource: J) {
//            observer.onChanged(resource)
//            if (resource.isEndState().orFalse()) {
//                liveData.removeObserver(this)
//            }
//        }
//    }
//    observe(lifecycleOwner, internalObserver)
//}
