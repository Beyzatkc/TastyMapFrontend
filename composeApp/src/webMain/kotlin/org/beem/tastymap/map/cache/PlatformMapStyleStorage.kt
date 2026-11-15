package org.beem.tastymap.map.cache

actual class PlatformMapStyleStorage : MapStyleStorage {
    private val memoryCache = mutableMapOf<String, String>()

    override fun getCachedStyle(key: String): String? = memoryCache[key]

    override fun saveStyle(key: String, json: String) {
        memoryCache[key] = json
    }
}