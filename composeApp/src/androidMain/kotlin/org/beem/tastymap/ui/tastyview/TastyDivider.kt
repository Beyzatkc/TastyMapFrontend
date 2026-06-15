package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

actual class TastyDivider actual constructor(
    override val modifier: TastyModifier,
    private val color: String,
    private val thickness: Int,
) : TastyView {

    override actual fun render(): TastyPlatformView {
        val parseColor = { hex: String ->
            try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { Color.Gray }
        }
        val composeColor = parseColor(color)

        return TastyPlatformView {
            HorizontalDivider(
                modifier = modifier.toAndroidModifier(),
                thickness = thickness.dp,
                color = composeColor
            )
        }
    }
}