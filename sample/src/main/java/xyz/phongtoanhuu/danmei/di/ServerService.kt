package xyz.phongtoanhuu.danmei.di

import androidx.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Headers
import xyz.phongtoanhuu.danmei.BuildConfig
import xyz.phongtoanhuu.danmei.response.CategoryResponse
import xyz.phongtoanhuu.danmei.utils.GenericApiResponse

interface ServerService {

    @Headers("Authorization: Bearer ${BuildConfig.JwtToken}")
    @GET("categories")
    fun getCategories(): LiveData<GenericApiResponse<List<CategoryResponse>>>
}