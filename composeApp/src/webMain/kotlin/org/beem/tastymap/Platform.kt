package org.beem.tastymap

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.header
import org.beem.tastymap.core.network.JsFetchCredentials

// Tarayıcının global fetch fonksiyonunu her zaman 'credentials: include' kullanacak şekilde sarmalar
@JsFun("""
    () => {
        if (typeof window !== 'undefined' && !window._fetchPatched) {
            const originalFetch = window.fetch;
            window.fetch = function(input, init) {
                init = init || {};
                init.credentials = 'include';
                return originalFetch.call(this, input, init);
            };
            window._fetchPatched = true;
        }
    }
""")
private external fun patchFetchCredentials()

class WebPlatform : Platform {
    override val name: String = "Web Platform"
    override val isWeb: Boolean = true
}

actual fun getPlatform(): Platform = WebPlatform()

actual fun HttpClientConfig<*>.platformConfig() {
    patchFetchCredentials()

    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }
    install(DefaultRequest) {
        header("X-Client-Type", "WEB")
        setAttributes {
            this.put(JsFetchCredentials, "include")
        }
    }
}