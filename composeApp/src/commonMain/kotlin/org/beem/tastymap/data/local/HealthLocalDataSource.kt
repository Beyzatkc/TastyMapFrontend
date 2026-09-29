package org.beem.tastymap.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.beem.tastymap.core.provider.DispatcherProvider
import org.beem.tastymap.data.mapper.toAllergyList
import org.beem.tastymap.data.mapper.toJsonString
import org.beem.tastymap.data.model.health.AllergyInfo
import org.beem.tastymap.data.model.health.HealthResponse
import org.beem.tastymap.sqldelight.HealthEntityQueries
import kotlin.time.Clock

class HealthLocalDataSource(
    private val queries: HealthEntityQueries,
    private val dispatchers: DispatcherProvider
) {
    suspend fun saveHealthInfo(userId: Long, health: HealthResponse) = withContext(dispatchers.io) {
        val now = Clock.System.now().toEpochMilliseconds()

        queries.insertOrUpdateHealth(
            userId = userId,
            hasDiabetes = when (health.hasDiabetes) {
                true -> 1L
                false -> 0L
                null -> null
            },
            eatType = health.eatType,
            allergyInfo = health.allergyInfo.toJsonString(),
            updatedAt = now
        )
    }

    suspend fun updateHealthPartiallyLocally(
        userId: Long,
        hasDiabetes: Boolean? = null,
        eatType: String? = null,
        allergyInfo: List<AllergyInfo>? = null
    ) = withContext(dispatchers.io) {
        val now = Clock.System.now().toEpochMilliseconds()

        queries.updatePartialHealth(
            hasDiabetes = when (hasDiabetes) {
                true -> 1L
                false -> 0L
                null -> null
            },
            eatType = eatType,
            allergyInfo = allergyInfo?.toJsonString(),
            updatedAt = now,
            userId = userId
        )
    }

    fun getHealthInfo(userId: Long): Flow<HealthResponse?> {
        return queries.getHealthByUserId(userId)
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
            .map { entity ->
                entity?.let {
                    HealthResponse(
                        hasDiabetes = when (it.hasDiabetes) {
                            1L -> true
                            0L -> false
                            else -> null
                        },
                        eatType = it.eatType,
                        allergyInfo = it.allergyInfo.toAllergyList()
                    )
                }
            }
    }
}