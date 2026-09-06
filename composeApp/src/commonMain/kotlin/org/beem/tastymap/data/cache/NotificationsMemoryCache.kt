package org.beem.tastymap.data.cache

import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.socialnotifications.SocialNotificationsResponse
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class NotificationsMemoryCache {

    private val cache = mutableMapOf<Int, CacheEntry>()

    private data class CacheEntry(
        val data: PageResponse<SocialNotificationsResponse>,
        val createdAt: TimeMark
    )

    fun get(page: Int): PageResponse<SocialNotificationsResponse>? {
        val entry = cache[page] ?: return null
        return entry.data
    }

    fun put(page: Int, response: PageResponse<SocialNotificationsResponse>) {
        cache[page] = CacheEntry(
            data = response,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    fun clear() {
        cache.clear()
    }
}