package org.beem.tastymap.map

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap

fun createMarkerBitmap(context: Context, resId: Int, sizeDp: Int = 36): android.graphics.Bitmap? {
    val drawable = ContextCompat.getDrawable(context, resId) ?: return null

    val density = context.resources.displayMetrics.density
    val targetHeightPx = (sizeDp * density).toInt()

    // Orijinal en-boy oranını koru
    val intrinsicWidth = drawable.intrinsicWidth.toFloat()
    val intrinsicHeight = drawable.intrinsicHeight.toFloat()
    val aspectRatio = if (intrinsicHeight > 0) intrinsicWidth / intrinsicHeight else 1f

    val targetWidthPx = (targetHeightPx * aspectRatio).toInt().coerceAtLeast(1)

    val bitmap = createBitmap(targetWidthPx, targetHeightPx.coerceAtLeast(1))
    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}