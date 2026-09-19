package org.beem.tastymap.data.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.beem.tastymap.domain.model.UserProfile
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class ProfileMemoryCache {

    private val cache = mutableMapOf<Long, CacheEntry>()
    private val mutex = Mutex()

    private val defaultTtl = 5.minutes

    private data class CacheEntry(
        val data: UserProfile,
        val createdAt: TimeMark
    )

    suspend fun get(userId: Long): UserProfile? = mutex.withLock {
        val entry = cache[userId] ?: return@withLock null

        if (entry.createdAt.elapsedNow() > defaultTtl) {
            cache.remove(userId)
            return@withLock null
        }

        entry.data
    }

    suspend fun put(userId: Long?, profile: UserProfile) {
        if (userId == null) return

        mutex.withLock {
            cache[userId] = CacheEntry(
                data = profile,
                createdAt = TimeSource.Monotonic.markNow()
            )
        }
    }


    suspend fun clear() = mutex.withLock {
        cache.clear()
    }
}