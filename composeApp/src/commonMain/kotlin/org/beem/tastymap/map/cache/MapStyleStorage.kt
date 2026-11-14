package org.beem.tastymap.map.cache

interface MapStyleStorage {
    fun getCachedStyle(key: String): String?
    fun saveStyle(key: String, json: String)
}