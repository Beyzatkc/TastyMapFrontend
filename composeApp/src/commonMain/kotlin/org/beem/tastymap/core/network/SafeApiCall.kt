package org.beem.tastymap.core.network

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import org.beem.tastymap.data.model.auth.ErrorResponse

suspend fun <T> safeApiCall(
    call: suspend () -> T
): ResultWrapper<T> = withContext(Dispatchers.Default) {
    try {
        ResultWrapper.Success(call())
    } catch (e: Exception) {
        e.printStackTrace()
        when (e) {
            is IOException, is io.ktor.client.network.sockets.ConnectTimeoutException -> {
                val message = e.message ?: "Bilinmeyen ağ hatası"

                ResultWrapper.Error(
                    "İnternet bağlantınızı kontrol edin. ($message)",
                    ErrorType.NETWORK_ERROR
                )
            }

            is ResponseException -> {
                val errorResponse = try {
                    e.response.body<ErrorResponse>()
                } catch (_: Exception) {
                    null
                }

                println("Hata %: ${e.message}")
                val errorMessage = errorResponse?.message ?: "Sunucu hatası: ${e.response.status.value}"

                if (errorResponse?.error == "EMAIL_NOT_VERIFIED") {
                    ResultWrapper.Error(
                        message = errorMessage,
                        type = ErrorType.EMAIL_NOT_VERIFIED,
                        email = errorResponse.email
                    )
                } else {
                    ResultWrapper.Error(errorMessage, ErrorType.SERVER_ERROR)
                }
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