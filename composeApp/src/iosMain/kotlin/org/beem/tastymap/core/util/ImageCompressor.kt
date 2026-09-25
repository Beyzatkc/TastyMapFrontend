package org.beem.tastymap.core.util

import kotlinx.cinterop.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.*
import platform.posix.memcpy

actual object ImageCompressor {

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun compress(
        bytes: ByteArray,
        maxWidth: Int = 1280,
        maxHeight: Int = 1280,
        quality: Int = 75
    ): ByteArray = withContext(Dispatchers.IO) {
        runCatching {
            val data = bytes.toNSData()
            val image = UIImage.imageWithData(data) ?: return@withContext bytes

            val originalWidth = image.size.useContents { width }
            val originalHeight = image.size.useContents { height }

            // Eğer görsel zaten küçükse sadece kalite sıkıştırması yap
            if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
                val compressedData = UIImageJPEGRepresentation(image, quality / 100.0)
                return@runCatching compressedData?.toByteArray() ?: bytes
            }

            // Yeni boyutları hesapla
            val ratio = minOf(maxWidth.toDouble() / originalWidth, maxHeight.toDouble() / originalHeight)
            val newWidth = originalWidth * ratio
            val newHeight = originalHeight * ratio

            // Görseli yeniden boyutlandır
            UIGraphicsBeginImageContextWithOptions(CGSizeMake(newWidth, newHeight), false, 1.0)
            image.drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
            val newImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()

            val finalImage = newImage ?: image

            // JPEG olarak sıkıştır (iOS'te kalite 0.0 ile 1.0 arasındadır)
            val compressedData = UIImageJPEGRepresentation(finalImage, quality / 100.0)

            compressedData?.toByteArray() ?: bytes
        }.getOrDefault(bytes)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun ByteArray.toNSData(): NSData {
    if (this.isEmpty()) return NSData()
    val pinned = this.pin()
    return NSData.create(
        bytesNoCopy = pinned.addressOf(0),
        length = this.size.toULong(),
        deallocator = { _, _ -> pinned.unpin() }
    )
}

@OptIn(ExperimentalForeignApi::class)
internal fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) return ByteArray(0)
    val byteArray = ByteArray(length)
    byteArray.usePinned { pinned ->
        memcpy(pinned.addressOf(0), this.bytes, this.length)
    }
    return byteArray
}