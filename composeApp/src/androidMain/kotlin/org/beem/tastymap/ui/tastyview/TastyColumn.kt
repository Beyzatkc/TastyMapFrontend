package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

actual class TastyColumn actual constructor(
    private val modifier: TastyModifier,
    private val children: List<TastyView>
) : TastyView {

    override actual fun render(): TastyPlatformView {
        return TastyPlatformView {
            val nativeModifier = modifier.toAndroidModifier()

            val verticalGap = modifier.gap.dp

            val horizontalAlign = when (modifier.alignItems) {
                "center" -> Alignment.CenterHorizontally
                "flex-end" -> Alignment.End
                else -> Alignment.Start
            }

            Column(
                modifier = nativeModifier,
                verticalArrangement = Arrangement.spacedBy(verticalGap),
                horizontalAlignment = horizontalAlign
            ) {
                children.forEach { child ->
                    child.render().content()
                }
            }
        }
    }
}