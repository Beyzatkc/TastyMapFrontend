package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

actual class TastyRow actual constructor(
    private val modifier: TastyModifier,
    private val children: List<TastyView>
) : TastyView {

    override actual fun render(): TastyPlatformView {
        return TastyPlatformView {
            val nativeModifier = modifier.toAndroidModifier()
            val horizontalGap = modifier.gap.dp

            val horizontalArrangement = when (modifier.justifyContent) {
                "space-between" -> Arrangement.SpaceBetween
                "center" -> Arrangement.Center
                "flex-end" -> Arrangement.End
                else -> Arrangement.spacedBy(horizontalGap)
            }

            val verticalAlign = when (modifier.alignItems) {
                "center" -> Alignment.CenterVertically
                "flex-end" -> Alignment.Bottom
                else -> Alignment.Top
            }

            Row(
                modifier = nativeModifier, // 🚀 Kendi modifier'ımız burada da parlıyor!
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = verticalAlign
            ) {
                children.forEach { child ->
                    child.render().content()
                }
            }
        }
    }
}