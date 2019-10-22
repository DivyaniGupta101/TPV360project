package com.inexture.baseproject.data

import androidx.lifecycle.LiveData
import com.inexture.baseproject.App
import com.inexture.baseproject.model.Repo
import com.inexture.baseproject.network.ApiClient
import com.inexture.baseproject.network.resources.*
import kotlinx.coroutines.CoroutineScope

object AppRepository {

    fun CoroutineScope.searchRepositories(
        query: String,
        data: List<Repo>?,
        page: Int = 1,
        forceLoadFromNetwork: Boolean = false
    ): LiveData<Resource<List<Repo>?>> =

        dataApi<List<Repo>?, APIError>(data)
        {

            // get data from network
            fromNetwork {
                ApiClient.service.searchRepos(query, page).getResult()
                    .map { it?.items?.sortedBy { it.id } }
            }

            if (!forceLoadFromNetwork) {
                // get data from local db
                fromLocal { App.db.repoDao().getQueriedResult("%$query%").sortedBy { it.id } }

                // save data in local db
                saveResultInLocal { data: List<Repo>? ->
                    data?.let {
                        App.db.repoDao().insertRepos(it)
                    }
                }
            }
        }

    fun CoroutineScope.getMultiUser(): LiveData<Resource<MultiUserResponse?>> =
        dataApi<MultiUserResponse?, APIError> {
            fromNetwork {
                ApiClient.service.getMultiUsers().getResult().map { it }
            }
        }
}