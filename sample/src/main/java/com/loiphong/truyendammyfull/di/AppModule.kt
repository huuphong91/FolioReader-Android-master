package com.loiphong.truyendammyfull.di

import androidx.room.Room
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.loiphong.truyendammyfull.base.BaseApplication
import com.loiphong.truyendammyfull.di.Properties.SERVER
import com.loiphong.truyendammyfull.persistence.AppDatabase
import com.loiphong.truyendammyfull.repository.MainRepository
import com.loiphong.truyendammyfull.utils.AppExecutors
import com.loiphong.truyendammyfull.utils.InterstitialAdUtils
import com.loiphong.truyendammyfull.utils.LiveDataCallAdapterFactory
import com.loiphong.truyendammyfull.utils.SharePreferenceHelper
import com.loiphong.truyendammyfull.viewmodel.MainViewModel
import java.util.concurrent.TimeUnit

val appModule = module {
    single { createOkHttpClient() }
    single { createWebService<ServerService>(okHttpClient = get(), url = getProperty(SERVER)) }
    single { provideAppDatabase(application = get()) }
    single { androidApplication() as BaseApplication }
    single { SharePreferenceHelper(get()) }
    single { InterstitialAdUtils(get(), get()) }
    single { InterstitialAd(get()) }
    single { AdRequest.Builder().build() }
    single { AppExecutors() }
}

val fragmentModule = module {
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}

val repositoryModule = module {
    single { MainRepository(get(), get(), get(), get(), get()) }
}

object Properties {
    const val SERVER = "SERVER"
}

fun provideAppDatabase(application: BaseApplication): AppDatabase {
    return Room
        .databaseBuilder(application, AppDatabase::class.java, "database.db")
        .fallbackToDestructiveMigration()
        .build()
}

fun createOkHttpClient(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return OkHttpClient.Builder()
        .connectTimeout(30L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor).build()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(T::class.java)
}