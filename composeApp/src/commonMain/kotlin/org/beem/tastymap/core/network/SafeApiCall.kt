package org.beem.tastymap.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

suspend fun <T> safeApiCall(
    call: suspend () -> T
): ResultWrapper<T> {
    return withContext(Dispatchers.Default) {
        try {
            ResultWrapper.Success(call())
        } catch (e: Exception) {
            when (e) {
                is io.ktor.client.network.sockets.ConnectTimeoutException,
                is io.ktor.utils.io.errors.IOException -> {
                    ResultWrapper.Error("Bağlantı hatası", ErrorType.NETWORK_ERROR)
                }
                is io.ktor.client.plugins.ResponseException -> {
                    ResultWrapper.Error("Sunucu hatası: ${e.response.status.value}", ErrorType.SERVER_ERROR)
                }
                else -> {
                    ResultWrapper.Error(e.message ?: "Bilinmeyen hata", ErrorType.SERVER_ERROR)
                }
            }
        }
    }
}