package xyz.phongtoanhuu.danmei.base

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import xyz.phongtoanhuu.danmei.di.appModule
import xyz.phongtoanhuu.danmei.di.fragmentModule
import xyz.phongtoanhuu.danmei.di.repositoryModule
import xyz.phongtoanhuu.danmei.di.viewModelModule

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
