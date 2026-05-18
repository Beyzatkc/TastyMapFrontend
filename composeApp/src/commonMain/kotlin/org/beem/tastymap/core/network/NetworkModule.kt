

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.util.AppConfig
import org.beem.tastymap.data.model.RefreshTokenResponseDTO
import org.beem.tastymap.getPlatform
import org.beem.tastymap.platformConfig

val platform = getPlatform()
fun HttpClientConfig<*>.commonConfig() {
    platformConfig()
    expectSuccess = true
    install(HttpTimeout) {
        requestTimeoutMillis = 10000
        connectTimeoutMillis = 10000
        socketTimeoutMillis = 10000
    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        })
    }
    install(Logging) {
        level = LogLevel.ALL
        logger = Logger.DEFAULT
    }
    install(DefaultRequest) {
        url(AppConfig.BASE_URL)
        header("ngrok-skip-browser-warning", "true")
        header("Content-Type", "application/json")

        if (platform.isWeb) {
            header("X-Client-Type", "WEB")
        } else {
            header("X-Client-Type", "MOBILE")
        }
    }
}

fun createNoAuthClient() = HttpClient {
    commonConfig()
}
fun createAuthClient(tokenManager: TokenManager, noAuthClient: HttpClient) = HttpClient {
    commonConfig()

    if (!platform.isWeb) {
        install(DefaultRequest) {
            tokenManager.getAccessToken()?.let { token ->
                header("Authorization", "Bearer $token")
            }
        }

        install(Auth) {
            bearer {
                loadTokens {
                    if (platform.isWeb) return@loadTokens null

                    val access = tokenManager.getAccessToken()
                    val refresh = tokenManager.getRefreshToken()
                    if (access != null && refresh != null) BearerTokens(access, refresh) else null
                }


                refreshTokens {
                    val deviceId = tokenManager.getDeviceId() ?: "unknown_device"
                    val refreshToken = tokenManager.getRefreshToken()

                    try {
                        val response = noAuthClient.post("api/users/refresh") {
                            setBody(buildMap {
                                put("deviceId", deviceId)
                                put("refreshToken", refreshToken ?: "")
                            })
                        }

                        if (response.status == HttpStatusCode.OK) {
                            val newTokens = response.body<RefreshTokenResponseDTO>()
                            tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                            BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                        } else {
                            tokenManager.clear()
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
