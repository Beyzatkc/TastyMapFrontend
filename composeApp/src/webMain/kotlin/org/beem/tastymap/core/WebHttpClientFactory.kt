package org.beem.tastymap.core

import commonConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import org.beem.tastymap.core.auth.AuthEventBus
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.provider.HttpClientFactory
import org.beem.tastymap.data.model.auth.ErrorResponse

class WebHttpClientFactory(
    private val deviceInfoProvider: DeviceInfoProvider,
    private val authEventBus: AuthEventBus
) : HttpClientFactory {

    override fun createAuthClient(
        noAuthClient: HttpClient
    ): HttpClient = HttpClient {
        commonConfig()
    }.apply {
        plugin(HttpSend).intercept { request ->

            val originalCall = execute(request)

            val isRefreshEndpoint =
                request.url.encodedPath.contains("/api/users/refresh")

            if (
                !isRefreshEndpoint &&
                originalCall.response.status == HttpStatusCode.Unauthorized
            ) {
                // Öncellikle orijinal isteğin döndüğü hatayı oku (PASSWORD_CHANGED burada yazıyor olabilir)
                val originalError = runCatching {
                    originalCall.response.body<ErrorResponse>()
                }.getOrNull()

                // Eğer orijinal yanıt zaten Şifre Değişti veya Çıkış Yapıldı hatasıysa doğrudan bildirim at
                if (originalError?.error == "PASSWORD_CHANGED") {
                    authEventBus.emit(AuthEventBus.AuthEvent.OnPasswordChanged)
                    return@intercept originalCall
                } else if (originalError?.error == "LOGGED_OUT") {
                    authEventBus.emit(AuthEventBus.AuthEvent.OnLoggedOut)
                    return@intercept originalCall
                }

                try {
                    val deviceId = deviceInfoProvider.getDeviceId()

                    val refreshResponse =
                        noAuthClient.post("/api/users/refresh") {
                            contentType(ContentType.Application.Json)
                            setBody(mapOf("deviceId" to deviceId))
                        }

                    if (refreshResponse.status == HttpStatusCode.OK) {
                        execute(request)
                    } else {
                        // Refresh isteğinden dönen hatayı okumaya çalış
                        val refreshError = runCatching {
                            refreshResponse.body<ErrorResponse>()
                        }.getOrNull()

                        val finalError = refreshError?.error ?: originalError?.error

                        when (finalError) {
                            "PASSWORD_CHANGED" ->
                                authEventBus.emit(AuthEventBus.AuthEvent.OnPasswordChanged)

                            "LOGGED_OUT" ->
                                authEventBus.emit(AuthEventBus.AuthEvent.OnLoggedOut)

                            else ->
                                authEventBus.emit(AuthEventBus.AuthEvent.OnSessionExpired)
                        }

                        originalCall
                    }

                } catch (e: Exception) {
                    authEventBus.emit(AuthEventBus.AuthEvent.OnSessionExpired)
                    originalCall
                }

            } else {
                originalCall
            }
        }
    }
}