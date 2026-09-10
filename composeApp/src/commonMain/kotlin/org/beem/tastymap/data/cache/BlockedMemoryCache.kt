package org.beem.tastymap.data.cache

import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.block.BlockResponse
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class BlockedMemoryCache {

    private val cache = mutableMapOf<Int, CacheEntry>()

    private data class CacheEntry(
        val data: PageResponse<BlockResponse>,
        val createdAt: TimeMark
    )

    fun get(page: Int): PageResponse<BlockResponse>? {
        val entry = cache[page] ?: return null
        return entry.data
    }

    fun put(page: Int, response: PageResponse<BlockResponse>) {
        cache[page] = CacheEntry(
            data = response,
            createdAt = TimeSource.Monotonic.markNow()
        )
    }

    fun clear() {
        cache.clear()
    }
}