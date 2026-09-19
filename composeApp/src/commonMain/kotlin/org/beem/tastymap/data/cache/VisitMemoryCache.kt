package org.beem.tastymap.data.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.visit.VisitResponse
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class VisitMemoryCache() {
    private val cache = mutableMapOf<Int, CacheEntry>()

    private val expirationDuration: Duration = 5.minutes
    private val mutex = Mutex()

    private data class CacheEntry(
        val data: PageResponse<VisitResponse>,
        val createdAt: TimeMark
    )

    suspend fun get(page: Int): PageResponse<VisitResponse>? = mutex.withLock {
        val entry = cache[page] ?: return@withLock null

        if (entry.createdAt.elapsedNow() > expirationDuration) {
            cache.remove(page)
            return@withLock null
        }

        return@withLock entry.data
    }

    suspend fun put(page: Int, response: PageResponse<VisitResponse>) = mutex.withLock {
        cache[page] = CacheEntry(
            data = response,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    suspend fun clear() = mutex.withLock {
        cache.clear()
    }
}