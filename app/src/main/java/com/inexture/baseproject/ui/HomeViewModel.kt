package com.inexture.baseproject.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.inexture.baseproject.data.AppRepository
import com.inexture.baseproject.model.Repo
import com.inexture.baseproject.network.resources.CoroutineScopedViewModel
import com.inexture.baseproject.network.resources.Resource
import com.inexture.baseproject.pagination.RepositorySource

class HomeViewModel : CoroutineScopedViewModel() {

    private var mQuery = MutableLiveData<String>()

    private var query = "jake"

    private var isRefresh = false


    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(true)
        .setPageSize(10).build()
    val repoList: LiveData<PagedList<Repo>>

    init {
        repoList = (LivePagedListBuilder(
            object : DataSource.Factory<Int, Repo>() {
                override fun create(): DataSource<Int, Repo> {
                    return RepositorySource(this@HomeViewModel, query)
                }
            }, pagedListConfig
        )).build()
    }

    fun getUsers(): LiveData<Resource<MultiUserResponse?>> = with(AppRepository) { getMultiUser() }

/*

    val results: LiveData<Resource<List<Repo>?>> = Transformations
        .switchMap(query) { search ->
            if (search.isNullOrBlank()) {
                null
            } else {
                with(AppRepository) {
                    val result = searchRepositories(search, list.value, 1, isRefresh)
                    isRefresh = false
                    result
                }
            }
        }


    val list: LiveData<List<Repo>> = Transformations.map(results) {
        it.data
    }

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim()
        mQuery.value = input
    }

    fun refreshRepos() {
        isRefresh = true
        setQuery(query.value.orEmpty())
    }
*/


}