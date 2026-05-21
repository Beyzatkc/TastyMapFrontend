package org.beem.tastymap

import android.os.Build
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.header

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val isWeb: Boolean = false
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual fun HttpClientConfig<*>.platformConfig() {
    install(DefaultRequest) {
        header("X-Client-Type", "MOBILE")
    }
}