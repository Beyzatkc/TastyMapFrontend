package org.beem.tastymap

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.js.*
import io.ktor.client.engine.js.JsClientEngineConfig
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies


class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val isWeb: Boolean = true
}

actual fun getPlatform(): Platform = WasmPlatform()


actual fun HttpClientConfig<*>.platformConfig() {
    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }
    (this as? HttpClientConfig<JsClientEngineConfig>)?.engine {

    }
}