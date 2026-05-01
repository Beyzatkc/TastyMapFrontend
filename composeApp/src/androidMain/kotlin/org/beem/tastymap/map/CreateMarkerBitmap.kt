package org.beem.tastymap.map

import android.content.Context

fun createMarkerBitmap(context: Context, resId: Int, sizeDp: Int): android.graphics.Bitmap? {
    val drawable = androidx.core.content.ContextCompat.getDrawable(context, resId) ?: return null

    val density = context.resources.displayMetrics.density
    val px = (sizeDp * density).toInt()

    val bitmap = android.graphics.Bitmap.createBitmap(px, px, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}