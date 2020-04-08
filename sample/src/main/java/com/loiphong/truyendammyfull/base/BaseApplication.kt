package com.loiphong.truyendammyfull.base

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import com.loiphong.truyendammyfull.di.appModule
import com.loiphong.truyendammyfull.di.fragmentModule
import com.loiphong.truyendammyfull.di.repositoryModule
import com.loiphong.truyendammyfull.di.viewModelModule

class BaseApplication : Application() {

    private val appComponent = listOf(appModule, viewModelModule, repositoryModule, fragmentModule)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            androidFileProperties()
            modules(appComponent)
        }
    }
}
