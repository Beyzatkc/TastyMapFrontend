package org.beem.tastymap

import JsFetchCredentials
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.header

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val isWeb: Boolean = true
}

actual fun getPlatform(): Platform = WasmPlatform()

// Parametre almayan, tamamen dışarıda bir top-level fonksiyon.
// Tarayıcının global fetch fonksiyonunu yakalayıp, giden her isteğe otomatik 'include' ekler.
private fun setupGlobalFetchCredentialsInterceptor() {
    js("""
        if (typeof window !== 'undefined' && !window._fetchInterceptorInstalled) {
            const originalFetch = window.fetch;
            window.fetch = function(resource, init) {
                const modifiedInit = init || {};
                modifiedInit.credentials = 'include';
                return originalFetch(resource, modifiedInit);
            };
            window._fetchInterceptorInstalled = true;
        }
    """)
}

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
    setupGlobalFetchCredentialsInterceptor()
}