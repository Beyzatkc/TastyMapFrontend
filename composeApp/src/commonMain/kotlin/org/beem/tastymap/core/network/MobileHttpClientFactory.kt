package org.beem.tastymap.core.network

import commonConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.beem.tastymap.core.auth.AuthEventBus
import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.provider.HttpClientFactory
import org.beem.tastymap.data.model.auth.ErrorResponse
import org.beem.tastymap.data.model.auth.RefreshTokenResponseDTO

class MobileHttpClientFactory(
    private val tokenManager: TokenManager,
    private val authEventBus: AuthEventBus
) : HttpClientFactory {

    override fun createAuthClient(noAuthClient: HttpClient): HttpClient {
        return HttpClient {
            commonConfig()


            install(Auth) {
                bearer {
                    sendWithoutRequest { request ->
                        true
                    }

                    loadTokens {
                        val access = tokenManager.getAccessToken()
                        val refresh = tokenManager.getRefreshToken()
                        if (access != null && refresh != null) BearerTokens(access, refresh) else null
                    }

                    refreshTokens {
                        val deviceId = tokenManager.getDeviceId() ?: "unknown_device"
                        val refreshToken = tokenManager.getRefreshToken()

                        println("--> [REFRESH DEBUG] 401 Yakalandı! Refresh başlatılıyor. RefreshToken: $refreshToken")

                        if (refreshToken.isNullOrEmpty()) {
                            println("--> [REFRESH DEBUG] Refresh token boş, yönlendiriliyor.")
                            authEventBus.emit(AuthEventBus.AuthEvent.OnSessionExpired)
                            return@refreshTokens null
                        }

                        try {
                            val response = noAuthClient.post("api/users/refresh") {
                                contentType(ContentType.Application.Json)
                                setBody(
                                    mapOf(
                                        "deviceId" to deviceId,
                                        "refreshToken" to refreshToken
                                    )
                                )
                            }

                            println("--> [REFRESH DEBUG] Refresh Yanıt Kodu: ${response.status}")

                            if (response.status == HttpStatusCode.OK) {
                                val newTokens = response.body<RefreshTokenResponseDTO>()
                                println("--> [REFRESH DEBUG] Başarılı! Yeni Access Token: ${newTokens.accessToken.take(10)}...")

                                tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshtoken)
                                BearerTokens(newTokens.accessToken, newTokens.refreshtoken)
                            } else {
                                val errorBody = response.body<String>()
                                println("--> [REFRESH DEBUG] Refresh İsteği Başarısız Oldu! Body: $errorBody")

                                authEventBus.emit(AuthEventBus.AuthEvent.OnSessionExpired)
                                null
                            }
                        } catch (e: Exception) {
                            println("--> [REFRESH DEBUG] Exception Fırlatıldı: ${e.message}")
                            e.printStackTrace()
                            authEventBus.emit(AuthEventBus.AuthEvent.OnSessionExpired)
                            null
                        }
                    }
                }
            }
        }
    }
}