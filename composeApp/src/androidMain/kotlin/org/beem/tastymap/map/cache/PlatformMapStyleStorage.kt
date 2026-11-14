package org.beem.tastymap.map.cache

import android.content.Context
import java.io.File

actual class PlatformMapStyleStorage(private val context: Context) : MapStyleStorage {
    private val cacheDir = File(context.filesDir, "map_styles").apply { mkdirs() }

    override fun getCachedStyle(key: String): String? {
        val file = File(cacheDir, "${key.hashCode()}.json")
        return if (file.exists()) file.readText() else null
    }

    override fun saveStyle(key: String, json: String) {
        val file = File(cacheDir, "${key.hashCode()}.json")
        file.writeText(json)
    }
}