
package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalArrangement

actual class TastyColumn actual constructor(
    override val modifier: TastyModifier,
    private val verticalArrangement: TastyVerticalArrangement,
    private val horizontalAlignment: TastyHorizontalAlignment,
    private val children: List<TastyView>
) : TastyView {

    override actual fun render(): TastyPlatformView {
        return TastyPlatformView {
            val nativeModifier = modifier.toAndroidModifier()

            val composeArrangement = when (verticalArrangement) {
                is TastyVerticalArrangement.TOP -> Arrangement.Top
                is TastyVerticalArrangement.BOTTOM -> Arrangement.Bottom
                is TastyVerticalArrangement.CENTER -> Arrangement.Center
                is TastyVerticalArrangement.SPACE_BETWEEN -> Arrangement.SpaceBetween
                is TastyVerticalArrangement.SPACE_AROUND -> Arrangement.SpaceAround
                is TastyVerticalArrangement.SPACE_EVENLY -> Arrangement.SpaceEvenly

                is TastyVerticalArrangement.SpacedBy -> Arrangement.spacedBy(verticalArrangement.spaceDp.dp)
            }

            val composeAlignment = when (horizontalAlignment) {
                TastyHorizontalAlignment.START -> Alignment.Start
                TastyHorizontalAlignment.CENTER -> Alignment.CenterHorizontally
                TastyHorizontalAlignment.END -> Alignment.End
            }

            Column(
                modifier = nativeModifier,
                verticalArrangement = composeArrangement,
                horizontalAlignment = composeAlignment
            ) {
                children.forEach { child ->
                    child.render().content()
                }
            }
        }
    }
}