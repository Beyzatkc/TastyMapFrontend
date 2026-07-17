package org.beem.tastymap.core.util

import io.ktor.util.AttributeKey

object AppConfig {
    //const val BASE_URL = "https://coleman-nonethic-marinda.ngrok-free.dev/"
    const val BASE_URL = "http://localhost:8080/"
    val apiHost: String = BuildKonfig.API_HOST
    val wsProtocol: String = BuildKonfig.WS_PROTOCOL
    fun getApiBaseUrl() = "https://$apiHost"
    fun getWebSocketUrl(deviceId: String) = "$wsProtocol://$apiHost/ws/auth/$deviceId"
}