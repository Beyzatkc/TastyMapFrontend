

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.beem.tastymap.core.local.TokenManager
import org.beem.tastymap.core.util.AppConfig
import org.beem.tastymap.data.model.RefreshTokenResponseDTO

fun createHTTPClient(tokenManager: TokenManager) = HttpClient {

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
    }

    install(Auth) {
        bearer {
            sendWithoutRequest { request ->
                val url = request.url.encodedPath
                        url.contains("/login") ||
                        url.contains("/register") ||
                        url.contains("/refresh")
            }
            loadTokens {
                val access = tokenManager.getAccessToken()
                val refresh = tokenManager.getRefreshToken()
                if (access != null && refresh != null) {
                    BearerTokens(access, refresh)
                } else null
            }

            refreshTokens {
                val deviceId = tokenManager.getDeviceId() ?: "unknown_device"
                val refreshToken = tokenManager.getRefreshToken()

                val response = client.post("/api/users/refresh") {
                    markAsRefreshTokenRequest()
                    setBody(mapOf(
                        "refreshToken" to refreshToken,
                        "deviceId" to deviceId
                    ))
                }

                if (response.status == HttpStatusCode.OK) {
                    val newTokens = response.body<RefreshTokenResponseDTO>()
                    tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)

                    BearerTokens(
                        accessToken = newTokens.accessToken,
                        refreshToken = newTokens.refreshToken
                    )
                } else {
                    tokenManager.clear()
                    null
                }
            }
        }
    }
}