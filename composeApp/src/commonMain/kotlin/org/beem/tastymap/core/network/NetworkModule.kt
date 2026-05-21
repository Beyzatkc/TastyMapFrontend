

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import org.beem.tastymap.core.util.AppConfig
import org.beem.tastymap.getPlatform
import org.beem.tastymap.platformConfig

val JsFetchCredentials = AttributeKey<String>("js.fetch.credentials")
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
    }
}

fun createNoAuthClient() = HttpClient {
    commonConfig()
}
