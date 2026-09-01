package org.beem.tastymap.data.cache


class CacheManager(
    private val profileMemoryCache: ProfileMemoryCache,
    private val healthMemoryCache: HealthMemoryCache
) {
    fun clearAllMemoryCaches() {
        profileMemoryCache.clear()
        healthMemoryCache.clear()
    }
}