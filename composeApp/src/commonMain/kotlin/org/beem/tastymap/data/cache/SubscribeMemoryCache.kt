package org.beem.tastymap.data.cache

import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.subscribers.SubscribeResponse
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class SubscribeMemoryCache {

    private val cache = mutableMapOf<String, CacheEntry>()
    private val defaultTtl = 5.minutes

    private data class CacheEntry(
        val data: PageResponse<SubscribeResponse>,
        val createdAt: TimeMark
    )

    private fun generateKey(userId: Long, type: String, page: Int): String {
        return "${userId}_${type}_$page"
    }

    fun get(userId: Long, type: String, page: Int): PageResponse<SubscribeResponse>? {
        val key = generateKey(userId, type, page)
        val entry = cache[key] ?: return null

        if (entry.createdAt.elapsedNow() > defaultTtl) {
            cache.remove(key)
            return null
        }

        return entry.data
    }

    fun put(userId: Long, type: String, page: Int, response: PageResponse<SubscribeResponse>) {
        val key = generateKey(userId, type, page)
        cache[key] = CacheEntry(
            data = response,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    fun invalidateUserCache(userId: Long) {
        cache.keys.removeAll { it.startsWith("${userId}_") }
    }

    fun clear() {
        cache.clear()
    }
}