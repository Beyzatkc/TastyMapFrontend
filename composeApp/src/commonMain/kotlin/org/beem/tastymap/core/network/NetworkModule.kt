
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import org.beem.tastymap.core.local.SettingsManager
import org.beem.tastymap.core.util.AppConfig
import org.beem.tastymap.platformConfig
import kotlin.time.Duration.Companion.seconds
import io.ktor.client.request.HttpSendPipeline
import org.beem.tastymap.core.network.plugins.LanguageHeaderPlugin

val JsFetchCredentials = AttributeKey<String>("js.fetch.credentials")
fun HttpClientConfig<*>.commonConfig(settingsManager: SettingsManager) {
    platformConfig()
    expectSuccess = true

    install(WebSockets) {
        pingInterval = 30.seconds
    }

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
        header("Accept-Language", settingsManager.languageCode.value.ifEmpty { "tr" })

    }
    install(LanguageHeaderPlugin) {
        this.settingsManager = settingsManager
    }

}

fun createNoAuthClient(settingsManager: SettingsManager) = HttpClient {
    commonConfig(settingsManager)
}
