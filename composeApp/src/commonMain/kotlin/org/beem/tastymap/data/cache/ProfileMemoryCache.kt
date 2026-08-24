package org.beem.tastymap.data.cache

import org.beem.tastymap.domain.model.UserProfile
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class ProfileMemoryCache {

    private val cache = mutableMapOf<Long, CacheEntry>()

    private val defaultTtl = 5.minutes

    private data class CacheEntry(
        val data: UserProfile,
        val createdAt: TimeMark
    )

    fun get(userId: Long): UserProfile? {
        val entry = cache[userId] ?: return null

        if (entry.createdAt.elapsedNow() > defaultTtl) {
            cache.remove(userId)
            return null
        }

        return entry.data
    }

    fun put(userId: Long, profile: UserProfile) {
        cache[userId] = CacheEntry(
            data = profile,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    fun invalidate(userId: Long) {
        cache.remove(userId)
    }

    fun clear() {
        cache.clear()
    }
}