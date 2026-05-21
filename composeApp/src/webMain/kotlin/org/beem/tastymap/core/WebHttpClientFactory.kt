package org.beem.tastymap.core

import commonConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.provider.HttpClientFactory


class WebHttpClientFactory(
    private val deviceInfoProvider: DeviceInfoProvider
) : HttpClientFactory {
    override fun createAuthClient(noAuthClient: HttpClient) = HttpClient {
        commonConfig()

        install(HttpRequestRetry) {
            retryOnExceptionOrServerErrors(maxRetries = 1)
            retryIf { _, response ->
                response.status == HttpStatusCode.Forbidden || response.status == HttpStatusCode.Unauthorized
            }
        }

        HttpResponseValidator {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Forbidden || response.status == HttpStatusCode.Unauthorized) {
                    try {
                        val deviceId = deviceInfoProvider.getDeviceId()
                        val refreshResponse = noAuthClient.post("api/users/refresh") {
                            setBody(mapOf("deviceId" to deviceId))
                        }

                        if (refreshResponse.status != HttpStatusCode.OK) {
                            throw Exception("Token expired")
                        }

                    } catch (e: Exception) {
                        //localstoragerı sıfırlaması gerekcek

                        // Buradan kullanıcıyı Login ekranına atacak bir tetikleyici göndermelisin.
                        // Örneğin bir "Global Event" veya "Navigation State" güncellemesi:
                        // AppEvents.navigateToLogin()

                        throw e
                    }
                }
            }
        }
    }
}