package com.tpv.android.utils.coroutine

import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * This holds all the coroutine scopes for the lifecycle owners
 * They are added dynamically when requested and removed when their lifecycle ends
 */
private val coroutineScopesForLifeCycleOwners = mutableMapOf<LifecycleOwner, CoroutineScope>()


/**
 * This will provide a scope for all the coroutines which are bound by the lifecycle
 */
val LifecycleOwner.coroutineScope: CoroutineScope
    get() =
        if (coroutineScopesForLifeCycleOwners[this] != null) {
            // first check if we already have the scope for the lifecycle owner out map
            // if so simply return it - as simple as that
            coroutineScopesForLifeCycleOwners[this]!!
        } else {
            // Now as we don't have the scope for the lifecycle owner, we need to create one
            // that is bound to the lifecycle of the lifecycle owner

            // create a new coroutine context for current lifecycle owner
            val job = Job()
            val coroutineScope = CoroutineScope(job + Dispatchers.Main)

            lifecycle.addObserver(GenericLifecycleObserver { source, event ->
                if (event == androidx.lifecycle.Lifecycle.Event.ON_DESTROY) {
                    // cancel the pending job and all its subsequent child coroutines recursively
                    job.cancel()

                    // remove this lifecycle owners and it's coroutine scope from the list as it's being destroyed
                    coroutineScopesForLifeCycleOwners.remove(source)
                }
            })

            // add the newly created scope to list
            coroutineScopesForLifeCycleOwners[this] = coroutineScope
            coroutineScope
        }
