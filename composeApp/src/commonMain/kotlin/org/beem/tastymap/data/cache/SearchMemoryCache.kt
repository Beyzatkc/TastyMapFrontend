package org.beem.tastymap.data.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.search.UserSearchResponse
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class SearchMemoryCache(
    private val defaultTtl: Duration = 5.minutes
) {
    private data class CacheKey(val keyword: String, val page: Int)

    private val searchCache = mutableMapOf<CacheKey, SearchCacheEntry>()
    private val mutex = Mutex()

    private data class SearchCacheEntry(
        val data: List<UserSearchResponse>,
        val createdAt: TimeMark
    )

    private var historyCache: HistoryCacheEntry? = null
    private data class HistoryCacheEntry(
        val data: PageResponse<UserSearchResponse>,
        val createdAt: TimeMark
    )


    suspend fun getSearch(keyword: String, page: Int): List<UserSearchResponse>? = mutex.withLock {
        val key = CacheKey(keyword, page)
        val entry = searchCache[key] ?: return@withLock null

        if (entry.createdAt.elapsedNow() > defaultTtl) {
            searchCache.remove(key)
            return@withLock null
        }
        return@withLock entry.data
    }

    suspend fun putSearch(keyword: String, page: Int, response: List<UserSearchResponse>) = mutex.withLock {
        val key = CacheKey(keyword, page)
        searchCache[key] = SearchCacheEntry(
            data = response,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    // --- ARAMA GEÇMİŞİ (HISTORY) ---

    suspend fun getHistory(): PageResponse<UserSearchResponse>? = mutex.withLock {
        val entry = historyCache ?: return@withLock null

        // History tarafına da TTL kontrolü eklendi
        if (entry.createdAt.elapsedNow() > defaultTtl) {
            historyCache = null
            return@withLock null
        }
        return@withLock entry.data
    }

    suspend fun putHistory(response: PageResponse<UserSearchResponse>) = mutex.withLock {
        historyCache = HistoryCacheEntry(
            data = response,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    suspend fun clearHistory() = mutex.withLock {
        historyCache = null
    }

    suspend fun clear() = mutex.withLock {
        searchCache.clear()
        historyCache = null
    }
}