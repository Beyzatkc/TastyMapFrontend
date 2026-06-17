// androidMain / org.beem.tastymap.ui.tastyview / TastyRow.kt
package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalArrangement
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalAlignment

actual class TastyRow actual constructor(
    override val modifier: TastyModifier,
    private val horizontalArrangement: TastyHorizontalArrangement,
    private val verticalAlignment: TastyVerticalAlignment,
    private val children: List<TastyView>
) : TastyView {

    override actual fun render(): TastyPlatformView {
        return TastyPlatformView {
            val nativeModifier = modifier.toAndroidModifier()

            // 🚀 Row İçin Yatay Düzenleme Eşlemesi
            val composeArrangement = when (horizontalArrangement) {
                is TastyHorizontalArrangement.Start -> Arrangement.Start
                is TastyHorizontalArrangement.End -> Arrangement.End
                is TastyHorizontalArrangement.Center -> Arrangement.Center
                is TastyHorizontalArrangement.SpaceBetween -> Arrangement.SpaceBetween
                is TastyHorizontalArrangement.SpaceAround -> Arrangement.SpaceAround
                is TastyHorizontalArrangement.SpaceEvenly -> Arrangement.SpaceEvenly

                is TastyHorizontalArrangement.SpacedBy -> Arrangement.spacedBy(horizontalArrangement.spaceDp.dp)
            }

            val composeAlignment = when (verticalAlignment) {
                TastyVerticalAlignment.TOP -> Alignment.Top
                TastyVerticalAlignment.CENTER -> Alignment.CenterVertically
                TastyVerticalAlignment.BOTTOM -> Alignment.Bottom
            }

            Row(
                modifier = nativeModifier,
                horizontalArrangement = composeArrangement,
                verticalAlignment = composeAlignment
            ) {
                children.forEach { child ->
                    val childWeight = child.modifier.weight
                    if(childWeight != null && childWeight > 0f) {
                        Row(
                            modifier = Modifier.weight(childWeight, fill = true),
                            verticalAlignment = composeAlignment,
                        ) {
                            child.render().content()
                        }
                    }
                    else {
                        child.render().content()
                    }
                }
            }
        }
    }
}