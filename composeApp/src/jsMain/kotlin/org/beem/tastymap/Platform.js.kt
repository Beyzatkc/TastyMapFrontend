package org.beem.tastymap

import io.ktor.client.HttpClientConfig

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
    override val isWeb: Boolean = true
}

actual fun getPlatform(): Platform = JsPlatform()
actual fun HttpClientConfig<*>.platformConfig() {
    println("DEBUG: jsMain platformConfig TETİKLENDİ!")
    engine {
        println("DEBUG: Engine içinde asDynamic ayarı yapılıyor...")
        this.asDynamic().withCredentials = true
    }
}