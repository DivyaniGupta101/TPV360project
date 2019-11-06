package com.tpv.android.network.resources.dataproviders


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tpv.android.network.resources.Failure
import com.tpv.android.network.resources.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.tpv.android.network.resources.Result
import com.tpv.android.network.resources.Success

/**
 * This function takes the function as an argument with the receiver type of PaginatedDataResource
 * and @return LiveData<Resource<List<R>>> type object
 * [PaginatedDataResource] class have the functions for get and store data
 */
fun <R, F> CoroutineScope.paginatedDataApi(
    data: List<R>? = null,
    from: PaginatedDataResource<R, F>.() -> Unit
): LiveData<Resource<List<R>, F>> {
    val dataResource = PaginatedDataResource<R, F>()
    from.invoke(dataResource)
    launch(Dispatchers.IO) {
        dataResource.init(data)
    }
    return dataResource.result
}


/**
 * This class have the functions to get data from the
 * Network and local db and insert the data in local db
 */
class PaginatedDataResource<R, F> {
    val result = MutableLiveData<Resource<List<R>, F>>()

    private var initialData: List<R>? = null
    private var funcFromNetwork: (suspend () -> Result<List<R>, F>)? = null

    internal suspend fun init(data: List<R>?) {
        initialData = data

        processNetworkRequest()
    }

    /**
     * process the network request
     */
    private suspend fun processNetworkRequest() {
        withContext(Dispatchers.Main) {
            if (initialData == null || initialData.orEmpty().isEmpty()) {
                result.value = (Resource.loading(initialData.orEmpty()))
            } else {
                result.value = (Resource.loadingMore(initialData.orEmpty()))
            }
        }
        val liveDataFromNetwork = funcFromNetwork?.invoke()  // init network request
        withContext(Dispatchers.Main) {
            result.value = liveDataFromNetwork?.mapToResource()
        }
    }

    /**
     * @return [Resource]
     * this is the extension function of repoListResource, it maps the repoListResource to
     * their appropriate resource
     */
    private fun Result<List<R>, F>.mapToResource(): Resource<List<R>, F> {
        return when (this) {
            is Success -> {
                val initialList = ArrayList(initialData.orEmpty())
                initialList.addAll(data.orEmpty())
                val resource = Resource.success<List<R>, F>(initialList)
                if (data.orEmpty().isEmpty()) resource.hasNextPage = false
                resource
            }
            is Failure -> {
                val resource = Resource.error(initialData, throwable, error)
                resource.hasNextPage = false
                resource
            }
        }
    }

    /**
     * this function takes the argument as a
     * function where the network request call happen
     */
    fun fromNetwork(
        func: suspend () -> Result<List<R>, F>
    ) {
        this.funcFromNetwork = func
    }
}