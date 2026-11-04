package org.beem.tastymap.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.beem.tastymap.core.provider.DispatcherProvider
import org.beem.tastymap.data.model.ai.AiChatMessageResponse
import org.beem.tastymap.data.model.ai.AiSessionSummaryDto
import org.beem.tastymap.data.model.ai.AiStreamEvent
import org.beem.tastymap.data.model.ai.RecommendationReq
import org.beem.tastymap.data.model.ai.SliceResponse
import org.beem.tastymap.data.remote.AiChatDataSource

class AiChatRepository(
    private val remoteDataSource: AiChatDataSource,
    private val dispatchers: DispatcherProvider
) {

    suspend fun getUserSessions(page: Int = 0, size: Int = 20): Result<SliceResponse<AiSessionSummaryDto>> {
        return withContext(dispatchers.io) {
            runCatching {
                remoteDataSource.getUserSessions(page, size)
            }
        }
    }

    suspend fun getSessionMessages(sessionId: String): Result<List<AiChatMessageResponse>> {
        return withContext(dispatchers.io) {
            runCatching {
                remoteDataSource.getSessionMessages(sessionId)
            }
        }
    }

    suspend fun deleteSession(sessionId: String): Result<Unit> {
        return withContext(dispatchers.io) {
            runCatching {
                remoteDataSource.deleteSession(sessionId)
            }
        }
    }

    fun streamRecommendation(request: RecommendationReq): Flow<AiStreamEvent> {
        return remoteDataSource.streamRecommendation(request)
            .flowOn(dispatchers.io)
    }
}