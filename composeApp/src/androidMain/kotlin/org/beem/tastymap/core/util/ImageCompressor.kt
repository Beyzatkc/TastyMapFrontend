package org.beem.tastymap.core.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import coil3.decode.DecodeUtils.calculateInSampleSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

actual object ImageCompressor {
    actual suspend fun compress(
            bytes: ByteArray,
            maxWidth: Int,
            maxHeight: Int,
            quality: Int
        ): ByteArray = withContext(Dispatchers.IO) {
            runCatching {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

                options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
                options.inJustDecodeBounds = false

                val decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                    ?: return@withContext bytes

                val scaledBitmap = scaleBitmap(decodedBitmap, maxWidth, maxHeight)

                val outputStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

                if (scaledBitmap != decodedBitmap) {
                    scaledBitmap.recycle()
                }
                decodedBitmap.recycle()

                outputStream.toByteArray()
            }.getOrDefault(bytes)
        }

        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val (height: Int, width: Int) = options.outHeight to options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }

        private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            if (width <= maxWidth && height <= maxHeight) return bitmap
            val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
            val newWidth = (width * ratio).toInt()
            val newHeight = (height * ratio).toInt()
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }

}