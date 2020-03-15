package xyz.phongtoanhuu.danmei.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.toPublisher
import okhttp3.ResponseBody
import xyz.phongtoanhuu.danmei.base.BaseApplication
import xyz.phongtoanhuu.danmei.di.ServerService
import xyz.phongtoanhuu.danmei.entity.CategoryEntity
import xyz.phongtoanhuu.danmei.extension.isConnectedToTheInternet
import xyz.phongtoanhuu.danmei.persistence.AppDatabase
import xyz.phongtoanhuu.danmei.response.CategoryResponse
import xyz.phongtoanhuu.danmei.utils.*
import java.io.File

class MainRepository(
    val serverService: ServerService,
    val appDatabase: AppDatabase,
    val application: BaseApplication,
    val appExecutors: AppExecutors,
    val sharePreferenceHelper: SharePreferenceHelper
) {

    fun getCategories(): LiveData<Resource<List<CategoryEntity>>> {
        return object :
            NetworkBoundResource<List<CategoryEntity>, List<CategoryResponse>>(appExecutors) {

            override fun saveCallResult(item: List<CategoryResponse>) {
                val dataDb = appDatabase.serverDao().getCategoriesNormal()
                if (dataDb.count() > item.count()) {
                    val dataDbArray = dataDb as ArrayList
                    item.forEach {
                        val categoryEntity = CategoryEntity(
                            id = it.id,
                            avatar = "$BASE_URL${it.avatar}",
                            description = it.description,
                            title = it.title,
                            url = "$BASE_URL${it.url}",
                            create_at = it.created_at
                        )
                        dataDbArray.remove(categoryEntity)
                    }
                    dataDbArray.count()
                    dataDbArray.forEach {
                        appDatabase.serverDao().deleteCategoryEntityIsNotReaded(it.id)
                    }
                } else {
                    item.forEach {
                        val categoryEntity = CategoryEntity(
                            id = it.id,
                            avatar = "$BASE_URL${it.avatar}",
                            description = it.description,
                            title = it.title,
                            url = "$BASE_URL${it.url}",
                            create_at = it.created_at
                        )
                        appDatabase.serverDao().insertCategory(categoryEntity)
                    }
                }
            }

            override fun shouldFetch(data: List<CategoryEntity>?): Boolean {
                return (data == null ||
                        data.isEmpty() ||
                        data.count() != sharePreferenceHelper.latestCountCategories)
                        && application.isConnectedToTheInternet()
            }

            override fun loadFromDb(): LiveData<List<CategoryEntity>> =
                appDatabase.serverDao().getCategories()

            override fun createCall(): LiveData<ApiResponse<List<CategoryResponse>>> =
                serverService.getCategories()
        }.asLiveData()
    }

    fun downloadEpub(categoryEntity: CategoryEntity): LiveData<Resource<CategoryEntity>> {
        return object : NetworkBoundResource<CategoryEntity, ResponseBody>(appExecutors) {
            override fun saveCallResult(item: ResponseBody) {
                val fileName = categoryEntity.url.takeLast(37)
                application.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                    it.write(item.bytes())
                }
                val file = File(application.filesDir, fileName)
                categoryEntity.externalStorageFile = file.absolutePath
                updateCategory(categoryEntity)
            }

            override fun shouldFetch(data: CategoryEntity?): Boolean {
                return data?.externalStorageFile == ""
            }

            override fun loadFromDb(): LiveData<CategoryEntity> {
                return appDatabase.serverDao().getCategoryEntity(categoryEntity.id)
            }

            override fun createCall(): LiveData<ApiResponse<ResponseBody>> {
                return serverService.downloadEpubContent(categoryEntity.url)
            }
        }.asLiveData()
    }

    fun categoriesCount(): LiveData<Resource<Int>> {
        return object : NetworkBoundResource<Int, Int>(appExecutors) {
            override fun saveCallResult(item: Int) {
                sharePreferenceHelper.latestCountCategories = item
            }

            override fun shouldFetch(data: Int?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Int> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResponse<Int>> {
                return serverService.getCategoriesCount()
            }
        }.asLiveData()
    }

    fun updateCategory(categoryEntity: CategoryEntity) {
        appExecutors.diskIO().execute {
            appDatabase.serverDao().updateCategoryEntity(categoryEntity)
        }
    }
}