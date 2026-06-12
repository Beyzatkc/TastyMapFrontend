package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.icons.TastyMapIcons

actual class TastyIcon actual constructor(
    private val icon: TastyMapIcons,
    private val color: String,
    private val sizePx: Int
) : TastyView {

    override actual fun render(): TastyPlatformView {
        val parseColor = { hex: String ->
            try {
                Color(android.graphics.Color.parseColor(hex))
            } catch (_: Exception) {
                Color(0xFFF59E0B)
            }
        }

        val composeColor = parseColor(color)

        return TastyPlatformView {
            val imageVector: ImageVector = when (icon) {
                TastyMapIcons.STAR -> Icons.Filled.Star
                TastyMapIcons.LOCATION -> Icons.Filled.LocationOn
                else -> Icons.Filled.Star
            }

            Icon(
                imageVector = imageVector,
                contentDescription = icon.name,
                modifier = Modifier.size(sizePx.dp),
                tint = composeColor
            )
        }
    }
}