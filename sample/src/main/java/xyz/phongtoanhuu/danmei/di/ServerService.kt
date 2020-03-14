package xyz.phongtoanhuu.danmei.di

import androidx.lifecycle.LiveData
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Streaming
import retrofit2.http.Url
import xyz.phongtoanhuu.danmei.BuildConfig
import xyz.phongtoanhuu.danmei.response.CategoryResponse
import xyz.phongtoanhuu.danmei.utils.ApiResponse

interface ServerService {

    @Headers("Authorization: Bearer ${BuildConfig.JwtToken}")
    @GET("categories")
    fun getCategories(): LiveData<ApiResponse<List<CategoryResponse>>>

    @Headers("Authorization: Bearer ${BuildConfig.JwtToken}")
    @GET("categories/count")
    fun getCategoriesCount(): LiveData<ApiResponse<Int>>

    @Streaming
    @GET
    fun downloadEpubContent(@Url stringUrl: String): LiveData<ApiResponse<ResponseBody>>
}