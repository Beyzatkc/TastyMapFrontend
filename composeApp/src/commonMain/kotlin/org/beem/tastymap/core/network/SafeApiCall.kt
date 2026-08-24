package org.beem.tastymap.core.network

import io.ktor.client.statement.bodyAsText
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
            println("TastyMap Network [EXCEPTION] -> Sınıf: ${e::class.simpleName} | Mesaj: ${e.message}")
            when (e) {
                is io.ktor.client.network.sockets.ConnectTimeoutException,
                is io.ktor.utils.io.errors.IOException -> {
                    println("TastyMap Network [TIMEOUT/IO] -> Bağlantı zaman aşımına uğradı veya koptu.")
                    ResultWrapper.Error("Bağlantı hatası", ErrorType.NETWORK_ERROR)
                }
                is io.ktor.client.plugins.ResponseException -> {
                    val errorBody = e.response.bodyAsText()

                    val cleanMessage = if (errorBody.contains("\"message\"")) {
                        errorBody.substringAfter("\"message\":\"").substringBefore("\"")
                    } else {
                        "Sunucu hatası: ${e.response.status.value}"
                    }

                    println("TastyMap Network [HTTP ${e.response.status}] -> Dönen Hata Gövdesi: $errorBody")

                    ResultWrapper.Error(cleanMessage, ErrorType.SERVER_ERROR)
                }
                is kotlinx.serialization.SerializationException -> {
                    println("TastyMap Network [SERIALIZATION ERROR] -> Model eşleşmedi! JSON Parse Detayı: ${e.message}")
                    ResultWrapper.Error("Veri işleme hatası oluştu.", ErrorType.SERVER_ERROR)
                }
                else -> {
                    println("TastyMap Network [UNKNOWN ERROR] -> Beklenmeyen hata: ${e.message}")
                    println("Gizli Hata Detayı: ${e.message}")
                    ResultWrapper.Error("Bir hata oluştu.", ErrorType.SERVER_ERROR)
                }
            }
        }
    }
}