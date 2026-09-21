package org.beem.tastymap.data.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.post.PostResponse
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

data class PostCacheKey(
    val userId: Long,
    val page: Int
)

class PostMemoryCache {
    private val cache = mutableMapOf<PostCacheKey, CacheEntry>()

    private val expirationDuration: Duration = 5.minutes
    private val mutex = Mutex()

    private data class CacheEntry(
        val data: PageResponse<PostResponse>,
        val createdAt: TimeMark
    )

    suspend fun get(userId: Long, page: Int): PageResponse<PostResponse>? = mutex.withLock {
        val key = PostCacheKey(userId, page)
        val entry = cache[key] ?: return@withLock null

        if (entry.createdAt.elapsedNow() > expirationDuration) {
            cache.remove(key)
            return@withLock null
        }

        return@withLock entry.data
    }

    suspend fun put(userId: Long, page: Int, response: PageResponse<PostResponse>) = mutex.withLock {
        val key = PostCacheKey(userId, page)
        cache[key] = CacheEntry(
            data = response,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    suspend fun clearUserCache(userId: Long) = mutex.withLock {
        val keysToRemove = cache.keys.filter { it.userId == userId }
        keysToRemove.forEach { cache.remove(it) }
    }

    suspend fun clearAll() = mutex.withLock {
        cache.clear()
    }
}