package org.beem.tastymap
import android.app.Application
import org.beem.tastymap.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TastyMapApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TastyMapApp)
            modules(appModule)
        }
    }
}