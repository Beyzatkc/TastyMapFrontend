package org.beem.tastymap.data.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.socialnotifications.SocialNotificationsResponse
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class NotificationsMemoryCache {

    private val cache = mutableMapOf<Int, CacheEntry>()

    private val expirationDuration: Duration = 5.minutes
    private val mutex = Mutex()

    private data class CacheEntry(
        val data: PageResponse<SocialNotificationsResponse>,
        val createdAt: TimeMark
    )

    suspend fun get(page: Int): PageResponse<SocialNotificationsResponse>? = mutex.withLock {
        val entry = cache[page] ?: return null
        if (entry.createdAt.elapsedNow() > expirationDuration) {
            cache.remove(page)
            return@withLock null
        }
        return@withLock entry.data
    }

    suspend fun put(page: Int, response: PageResponse<SocialNotificationsResponse>) = mutex.withLock {
        cache[page] = CacheEntry(
            data = response,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    suspend fun clear() = mutex.withLock {
        cache.clear()
    }
}