package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.tastyview.icons.TastyMapIcon
import org.jetbrains.compose.resources.vectorResource

actual class TastyIcon actual constructor(
    private val icon: TastyMapIcon,
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
            val vectorDrawable = icon.toAndroidRes()

            Icon(
                imageVector = vectorResource(vectorDrawable),
                contentDescription = icon.name,
                modifier = Modifier.size(sizePx.dp),
                tint = composeColor
            )
        }
    }
}