package org.beem.tastymap

import io.ktor.client.HttpClientConfig


class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val isWeb: Boolean = true
}

actual fun getPlatform(): Platform = WasmPlatform()


actual fun HttpClientConfig<*>.platformConfig() {
    install(io.ktor.client.plugins.cookies.HttpCookies)
}
