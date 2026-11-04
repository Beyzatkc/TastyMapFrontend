package org.beem.tastymap.data.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.subscribers.SubscribeResponse
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class SubscribeMemoryCache(
    private val defaultTtl: Duration = 5.minutes
) {
    private data class CacheKey(
        val userId: Long,
        val type: String,
        val page: Int
    )

    private data class CacheEntry(
        val data: PageResponse<SubscribeResponse>,
        val createdAt: TimeMark
    )

    private val cache = mutableMapOf<CacheKey, CacheEntry>()
    private val mutex = Mutex()

    suspend fun get(userId: Long, type: String, page: Int): PageResponse<SubscribeResponse>? = mutex.withLock {
        val key = CacheKey(userId, type, page)
        val entry = cache[key] ?: return@withLock null

        if (entry.createdAt.elapsedNow() > defaultTtl) {
            cache.remove(key)
            return@withLock null
        }

        return@withLock entry.data
    }

    suspend fun put(userId: Long, type: String, page: Int, response: PageResponse<SubscribeResponse>) = mutex.withLock {
        val key = CacheKey(userId, type, page)
        cache[key] = CacheEntry(
            data = response,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    suspend fun invalidateUserCache(userId: Long) = mutex.withLock {
        cache.keys.removeAll { it.userId == userId }
    }

    suspend fun clear() = mutex.withLock {
        cache.clear()
    }
}