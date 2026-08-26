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

            install(DefaultRequest) {
                tokenManager.getAccessToken()?.let { token ->
                    header("Authorization", "Bearer $token")
                }
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val access = tokenManager.getAccessToken()
                        val refresh = tokenManager.getRefreshToken()
                        if (access != null && refresh != null) BearerTokens(access, refresh) else null
                    }

                    refreshTokens {
                        val deviceId = tokenManager.getDeviceId() ?: "unknown_device"
                        val refreshToken = tokenManager.getRefreshToken()

                        try {
                            val response = noAuthClient.post("api/users/refresh") {
                                setBody(mapOf(
                                    "deviceId" to deviceId,
                                    "refreshToken" to (refreshToken ?: "")
                                ))
                            }

                            if (response.status == HttpStatusCode.OK) {
                                val newTokens = response.body<RefreshTokenResponseDTO>()
                                tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                                BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                            } else {

                                val errorResponse = runCatching { response.body<ErrorResponse>() }.getOrNull()

                                when (errorResponse?.error) {
                                    "PASSWORD_CHANGED" -> authEventBus.emit(AuthEventBus.AuthEvent.OnPasswordChanged)
                                    "LOGGED_OUT" -> authEventBus.emit(AuthEventBus.AuthEvent.OnLoggedOut)
                                    else -> authEventBus.emit(AuthEventBus.AuthEvent.OnSessionExpired)
                                }

                                null
                            }
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
            }
        }
    }
}