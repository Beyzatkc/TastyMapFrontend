package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

actual class TastyDragHandle actual constructor(
    private val barColor: String
) : TastyView {

    override actual fun render(): TastyPlatformView {
        val parseColor = { hex: String ->
            try {
                Color(android.graphics.Color.parseColor(hex))
            } catch (_: Exception) {
                Color(0xFFCCCCCC)
            }
        }

        val composeBarColor = parseColor(barColor)

        return TastyPlatformView {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .background(
                            color = composeBarColor,
                            shape = RoundedCornerShape(100.dp)
                        )
                )
            }
        }
    }
}