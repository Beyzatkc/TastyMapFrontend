package org.beem.tastymap.data.cache

import org.beem.tastymap.data.model.health.HealthResponse

class HealthMemoryCache {
    private var cachedHealth: Pair<Long, HealthResponse>? = null

    fun get(userId: Long): HealthResponse? {
        val current = cachedHealth ?: return null
        return if (current.first == userId) current.second else null
    }

    fun put(userId: Long, health: HealthResponse) {
        cachedHealth = Pair(userId, health)
    }

    fun invalidate(userId: Long) {
        if (cachedHealth?.first == userId) {
            cachedHealth = null
        }
    }

    fun clear() {
        cachedHealth = null
    }
}