package org.beem.tastymap.data.cache

import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.search.UserSearchResponse
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlin.time.Duration.Companion.minutes

class SearchMemoryCache {
    private data class CacheKey(val keyword: String, val page: Int)

    private val searchCache = mutableMapOf<CacheKey, SearchCacheEntry>()
    private data class SearchCacheEntry(
        val data: List<UserSearchResponse>,
        val createdAt: TimeMark
    )

    // Geçmiş sonuçları için (PageResponse döner)
    private var historyCache: HistoryCacheEntry? = null
    private data class HistoryCacheEntry(
        val data: PageResponse<UserSearchResponse>,
        val createdAt: TimeMark
    )

    fun getSearch(keyword: String, page: Int): List<UserSearchResponse>? {
        val key = CacheKey(keyword, page)
        val entry = searchCache[key] ?: return null

        if (entry.createdAt.elapsedNow() > 5.minutes) {
            searchCache.remove(key)
            return null
        }
        return entry.data
    }

    fun putSearch(keyword: String, page: Int, response: List<UserSearchResponse>) {
        val key = CacheKey(keyword, page)
        searchCache[key] = SearchCacheEntry(
            data = response,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    fun getHistory(): PageResponse<UserSearchResponse>? {
        val entry = historyCache ?: return null
        return entry.data
    }

    fun putHistory(response: PageResponse<UserSearchResponse>) {
        historyCache = HistoryCacheEntry(
            data = response,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    fun clearHistory() {
        historyCache = null
    }

    fun clear() {
        searchCache.clear()
        historyCache = null
    }
}