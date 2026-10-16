package org.beem.tastymap.core.network

import io.ktor.client.HttpClient
import org.beem.tastymap.core.provider.HttpClientFactory

class AuthHttpClientManager(
    private val factory: HttpClientFactory,
    private val noAuthClient: HttpClient
) {

    private var authClient: HttpClient? = null

    fun getClient(): HttpClient {
        return authClient
            ?: factory.createAuthClient(noAuthClient).also {
                authClient = it
            }
    }

    fun recreateClient(): HttpClient {
        close()

        return factory.createAuthClient(noAuthClient).also {
            authClient = it
        }
    }

    fun close() {
        authClient?.close()
        authClient = null
    }
}