package org.beem.tastymap.data.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.beem.tastymap.data.model.health.HealthResponse
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class HealthMemoryCache(
    private val expirationDuration: Duration = 5.minutes
) {
    private var cachedHealth: CacheEntry? = null
    private val mutex = Mutex()

    private data class CacheEntry(
        val userId: Long,
        val data: HealthResponse,
        val createdAt: TimeMark
    )

    suspend fun get(userId: Long): HealthResponse? = mutex.withLock {
        val entry = cachedHealth ?: return@withLock null

        if (entry.userId != userId) return@withLock null

        if (entry.createdAt.elapsedNow() > expirationDuration) {
            cachedHealth = null
            return@withLock null
        }

        return@withLock entry.data
    }

    suspend fun put(userId: Long, health: HealthResponse) = mutex.withLock {
        cachedHealth = CacheEntry(
            userId = userId,
            data = health,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    suspend fun invalidate(userId: Long) = mutex.withLock {
        if (cachedHealth?.userId == userId) {
            cachedHealth = null
        }
    }

    suspend fun clear() = mutex.withLock {
        cachedHealth = null
    }
}