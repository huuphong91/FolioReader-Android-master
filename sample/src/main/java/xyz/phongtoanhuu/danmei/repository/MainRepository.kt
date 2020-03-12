package xyz.phongtoanhuu.danmei.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.phongtoanhuu.danmei.di.ServerService
import xyz.phongtoanhuu.danmei.entity.CategoryEntity
import xyz.phongtoanhuu.danmei.extension.isConnectedToTheInternet
import xyz.phongtoanhuu.danmei.persistence.AppDatabase
import xyz.phongtoanhuu.danmei.response.CategoryResponse
import xyz.phongtoanhuu.danmei.utils.ApiSuccessResponse
import xyz.phongtoanhuu.danmei.utils.DataState
import xyz.phongtoanhuu.danmei.utils.GenericApiResponse
import xyz.phongtoanhuu.danmei.view.MainViewState

class MainRepository(
    val serverService: ServerService,
    val appDatabase: AppDatabase,
    val application: Application
) : JobManager("MainRepository") {

    fun getCategories(): LiveData<DataState<MainViewState>> {
        return object :
            NetworkBoundResource<List<CategoryResponse>, List<CategoryEntity>, MainViewState>(
                application.isConnectedToTheInternet(),
                isNetworkRequest = true,
                shouldCancelIfNoInternet = false,
                shouldLoadFromCache = true
            ) {

            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main) {

                    // finishing by viewing db cache
                    result.addSource(loadFromCache()) { viewState ->
                        viewState.categoryFields.isQueryInProgress = false

                        viewState.categoryFields.isQueryExhausted = true

                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<List<CategoryResponse>>) {
                val categories: ArrayList<CategoryEntity> = ArrayList()
                for (categoryResponse in response.body) {
                    categories.add(
                        CategoryEntity(
                            id = categoryResponse.id,
                            avatar = categoryResponse.avatar,
                            title = categoryResponse.title,
                            description = categoryResponse.description,
                            url = categoryResponse.url
                        )
                    )
                }
                updateLocalDb(categories)

                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<List<CategoryResponse>>> {
                return serverService.getCategories()
            }

            override fun loadFromCache(): LiveData<MainViewState> {
                return appDatabase.serverDao().getCategories()
                    .switchMap {
                        object : LiveData<MainViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = MainViewState(
                                    MainViewState.CategoryFields(
                                        categories = it,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<CategoryEntity>?) {
                // loop through list and update the local db
                if (cacheObject != null) {
                    withContext(Dispatchers.IO) {
                        for (category in cacheObject) {
                            try {
                                // Launch each insert as a separate job to be executed in parallel
                                launch {
                                    Log.d(
                                        "PostsCache",
                                        "updateLocalDb: inserting blog: ${category}"
                                    )
                                    appDatabase.serverDao().insertCategory(category)
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    "PostsCache",
                                    "updateLocalDb: error updating cache data on blog post with slug: ${category}. " +
                                            "${e.message}"
                                )
                                // Could send an error report here or something but I don't think you should throw an error to the UI
                                // Since there could be many blog posts being inserted/updated.
                            }
                        }
                    }
                } else {
                    Log.d("PostsCache", "updateLocalDb: blog post list is null")
                }
            }

            override fun setJob(job: Job) {
                addJob("getCategories", job)
            }
        }.asLiveData()
    }
}