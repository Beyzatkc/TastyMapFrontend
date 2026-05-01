package org.beem.tastymap
import android.app.Application
import org.beem.tastymap.core.di.appModule
import org.beem.tastymap.di.androidModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class TastyMapApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val myMaps = "https://api.maptiler.com/maps/019dbfbf-86a2-7d38-869e-bd6ebbcee298/style.json?key=DNr5GYdtJfA7ecaMmrh1"

        MapLibre.getInstance(this, myMaps, WellKnownTileServer.MapLibre)

        startKoin {
            androidLogger()
            androidContext(this@TastyMapApp)
            modules(appModule,androidModule)
        }
    }
}
