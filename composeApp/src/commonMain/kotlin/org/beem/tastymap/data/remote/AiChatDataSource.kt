package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import org.beem.tastymap.core.network.AuthHttpClientManager
import org.beem.tastymap.data.model.ai.AiChatMessageResponse
import org.beem.tastymap.data.model.ai.AiSessionSummaryDto
import org.beem.tastymap.data.model.ai.AiStreamEvent
import org.beem.tastymap.data.model.ai.AiStreamPayloadDto
import org.beem.tastymap.data.model.ai.HydratedPlaceCardDto
import org.beem.tastymap.data.model.ai.QuotaFeedbackPayloadDto
import org.beem.tastymap.data.model.ai.RecommendationReq
import org.beem.tastymap.data.model.ai.SliceResponse

class AiChatDataSource(private val authHttpClientManager: AuthHttpClientManager) {

    private val client: HttpClient
        get() = authHttpClientManager.getClient()

    private val sseJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Kullanıcının geçmiş sohbet oturumlarını sayfalı (Slice) olarak çeker.
     */
    suspend fun getUserSessions(page: Int = 0, size: Int = 20): SliceResponse<AiSessionSummaryDto> {
        return client.get("api/v1/ai/sessions") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    /**
     * Belirli bir oturumun mesajlarını ve canlı mekan kartlarını getirir.
     */
    suspend fun getSessionMessages(sessionId: String): List<AiChatMessageResponse> {
        return client.get("api/v1/ai/sessions/$sessionId/messages").body()
    }

    /**
     * Oturumu ve bağlı mesajları siler.
     */
    suspend fun deleteSession(sessionId: String) {
        client.delete("api/v1/ai/sessions/$sessionId")
    }

    fun streamRecommendation(request: RecommendationReq): Flow<AiStreamEvent> = flow {
        client.preparePost("api/v1/ai/chat/recommend") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Text.EventStream)
            setBody(request)
            timeout {
                requestTimeoutMillis = 120_000
                socketTimeoutMillis = 120_000
            }
        }.execute { response ->
            val channel: ByteReadChannel = response.bodyAsChannel()

            var currentEvent = "message"
            val dataBuilder = StringBuilder()

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break

                when {
                    line.startsWith("event:") -> {
                        currentEvent = line.substringAfter("event:").trim().lowercase()
                    }
                    line.startsWith("data:") -> {
                        val dataPart = line.substringAfter("data:").trim()
                        dataBuilder.append(dataPart)
                    }
                    line.isEmpty() -> {
                        val rawData = dataBuilder.toString()
                        dataBuilder.clear()

                        if (rawData.isNotEmpty()) {
                            try {
                                when (currentEvent) {
                                    "status" -> {
                                        val payload = sseJson.decodeFromString<AiStreamPayloadDto>(rawData)
                                        emit(AiStreamEvent.Status(payload.content))
                                    }
                                    "token" -> {
                                        val payload = sseJson.decodeFromString<AiStreamPayloadDto>(rawData)
                                        emit(AiStreamEvent.Token(payload.content))
                                    }
                                    "places" -> {
                                        val places = sseJson.decodeFromString<List<HydratedPlaceCardDto>>(rawData)
                                        emit(AiStreamEvent.Places(places))
                                    }
                                    "quota_info" -> {
                                        val quota = sseJson.decodeFromString<QuotaFeedbackPayloadDto>(rawData)
                                        emit(AiStreamEvent.Quota(quota.remainingQuotaPercentage))
                                    }
                                    "completed" -> {
                                        emit(AiStreamEvent.Completed)
                                    }
                                    "error" -> {
                                        val payload = sseJson.decodeFromString<AiStreamPayloadDto>(rawData)
                                        emit(AiStreamEvent.Error(payload.content.ifBlank { "Bir sorun oluştu." }))
                                    }
                                }
                            } catch (e: Exception) {
                                // Gerekirse loglanabilir
                            }
                        }
                        currentEvent = "message"
                    }
                }
            }
            emit(AiStreamEvent.Completed)
        }
    }


}