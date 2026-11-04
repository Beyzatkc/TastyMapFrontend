package org.beem.tastymap.core.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

expect object ImageCompressor {
    suspend fun compress(
        bytes: ByteArray,
        maxWidth: Int = 1280,
        maxHeight: Int = 1280,
        quality: Int = 75
    ): ByteArray
}