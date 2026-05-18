package org.beem.tastymap

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
    override val isWeb: Boolean = true
}

actual fun getPlatform(): Platform = JsPlatform()
actual fun HttpClientConfig<*>.platformConfig() {
    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }
    engine {
        println("DEBUG: Engine içinde asDynamic ayarı yapılıyor...")
        this.asDynamic().withCredentials = true
    }
}