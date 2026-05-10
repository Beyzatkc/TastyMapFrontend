package org.beem.tastymap

import io.ktor.client.HttpClientConfig

interface Platform {
    val name: String
    val isWeb: Boolean
}

expect fun getPlatform(): Platform
expect fun HttpClientConfig<*>.platformConfig()