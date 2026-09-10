package org.beem.tastymap.data.cache


class CacheManager(
    private val profileMemoryCache: ProfileMemoryCache,
    private val healthMemoryCache: HealthMemoryCache,
    private val subscribeMemoryCache: SubscribeMemoryCache,
    private val notificationsMemoryCache: NotificationsMemoryCache,
    private val blockedMemoryCache: BlockedMemoryCache,
) {
    fun clearAllMemoryCaches() {
        profileMemoryCache.clear()
        healthMemoryCache.clear()
        subscribeMemoryCache.clear()
        notificationsMemoryCache.clear()
        blockedMemoryCache.clear()
    }
}