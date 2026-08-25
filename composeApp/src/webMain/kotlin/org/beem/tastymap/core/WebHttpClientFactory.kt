package org.beem.tastymap.core

import commonConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import org.beem.tastymap.core.auth.AuthEventBus
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.provider.HttpClientFactory

class WebHttpClientFactory(
    private val deviceInfoProvider: DeviceInfoProvider,
    private val userManager: UserManager,
    private val authEventBus: AuthEventBus
) : HttpClientFactory {

    override fun createAuthClient(noAuthClient: HttpClient) = HttpClient {
        commonConfig()

        HttpResponseValidator {
            validateResponse { response ->
                val isRefreshEndpoint = response.call.request.url.encodedPath.contains("api/users/refresh")

                if (!isRefreshEndpoint && (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden)) {
                    try {
                        val deviceId = deviceInfoProvider.getDeviceId()
                        val refreshResponse = noAuthClient.post("api/users/refresh") {
                            setBody(mapOf("deviceId" to deviceId))
                        }

                        if (refreshResponse.status != HttpStatusCode.OK) {
                            throw Exception("Refresh token invalid or expired")
                        }
                    } catch (e: Exception) {
                        userManager.clear()
                        authEventBus.emitUnauthenticated()
                        throw e
                    }
                }
            }
        }
    }
}