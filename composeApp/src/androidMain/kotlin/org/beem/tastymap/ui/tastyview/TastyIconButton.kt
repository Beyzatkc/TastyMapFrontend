package org.beem.tastymap.ui.tastyview


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

actual class TastyIconButton actual constructor(
    private val iconHtml: String,
    private val backgroundColor: String,
    private val iconColor: String,
    private val onClick: () -> Unit
) : TastyView {

    override actual fun render(): TastyPlatformView {
        val parseColor = { hex: String ->
            try {
                Color(android.graphics.Color.parseColor(hex))
            } catch (_: Exception) {
                Color.Transparent
            }
        }

        val composeBgColor = parseColor(backgroundColor)
        val composeIconColor = parseColor(iconColor)

        return TastyPlatformView {
            val nativeIconText = when (iconHtml) {
                "&times;", "×", "x" -> "✕"
                "&plus;", "+" -> "+"
                "&minus;", "-" -> "−"
                else -> iconHtml
            }

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale = if (isPressed) 0.92f else 1.0f

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .background(
                        color = composeBgColor,
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nativeIconText,
                    color = composeIconColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}