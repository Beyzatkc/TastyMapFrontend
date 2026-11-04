package org.beem.tastymap.data.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.postcomment.CommentsResponse
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class PostCommentMemoryCache(
    private val defaultTtl: Duration = 5.minutes
) {
    private data class CacheKey(
        val postId: Long
    )
    data class CacheEntry(
        val data: PageResponse<CommentsResponse>,
        val createdAt: TimeMark
    )
    private val cache = mutableMapOf<CacheKey, CacheEntry>()
    private val mutex = Mutex()
    suspend fun get(postId: Long, page: Int): PageResponse<CommentsResponse>? = mutex.withLock {
        if (page != 0) return@withLock null

        val key = CacheKey(postId)
        val entry = cache[key] ?: return@withLock null

        if (entry.createdAt.elapsedNow() > defaultTtl) {
            cache.remove(key)
            return@withLock null
        }

        return@withLock entry.data
    }

    suspend fun put(postId: Long, page: Int, response: PageResponse<CommentsResponse>) = mutex.withLock {
        if (page == 0) {
            val key = CacheKey(postId)
            cache[key] = CacheEntry(
                data = response,
                createdAt = TimeSource.Monotonic.markNow()
            )
        }
    }

    suspend fun invalidatePostCache(postId: Long) = mutex.withLock {
        cache.remove(CacheKey(postId))
    }

    suspend fun clear() = mutex.withLock {
        cache.clear()
    }
}