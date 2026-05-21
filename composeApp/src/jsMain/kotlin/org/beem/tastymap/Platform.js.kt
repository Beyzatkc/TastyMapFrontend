package org.beem.tastymap

import JsFetchCredentials
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.header

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
    override val isWeb: Boolean = true
}

actual fun getPlatform(): Platform = JsPlatform()
actual fun HttpClientConfig<*>.platformConfig() {
    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }
    install(DefaultRequest) {
        header("X-Client-Type", "WEB")
        setAttributes {
            this.put(JsFetchCredentials, "include")
        }
    }

    engine {
        println("DEBUG: Engine içinde asDynamic ayarı yapılıyor...")
        this.asDynamic().withCredentials = true
    }
}