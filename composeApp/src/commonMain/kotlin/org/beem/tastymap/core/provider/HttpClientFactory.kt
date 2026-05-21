package org.beem.tastymap.core.provider

import io.ktor.client.HttpClient

interface HttpClientFactory {
    fun createAuthClient(noAuthClient: HttpClient): HttpClient
}