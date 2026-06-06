package org.beem.tastymap.core.network

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import org.beem.tastymap.data.model.ErrorResponse

suspend fun <T> safeApiCall(
    call: suspend () -> T
): ResultWrapper<T> = withContext(Dispatchers.Default) {
    try {
        ResultWrapper.Success(call())
    } catch (e: Exception) {
        when (e) {
            is IOException, is io.ktor.client.network.sockets.ConnectTimeoutException -> {
                ResultWrapper.Error("İnternet bağlantınızı kontrol edin.", ErrorType.NETWORK_ERROR)
            }

            is ResponseException -> {
                val errorMessage = try {
                    e.response.body<ErrorResponse>().message ?: "Sunucu hatası: ${e.response.status.value}"
                } catch (_: Exception) {
                    "Sunucu ile iletişimde bir sorun oluştu."
                }
                ResultWrapper.Error(errorMessage, ErrorType.SERVER_ERROR)
            }

            is SerializationException -> {
                ResultWrapper.Error("Veri formatı hatası.", ErrorType.SERVER_ERROR)
            }

            else -> {
                ResultWrapper.Error("Beklenmeyen bir hata oluştu.", ErrorType.UNKNOWN_ERROR)
            }
        }
    }
}