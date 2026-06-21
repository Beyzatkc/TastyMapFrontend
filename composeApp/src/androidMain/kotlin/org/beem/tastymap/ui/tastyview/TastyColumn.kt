
package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalArrangement

actual class TastyColumn actual constructor(
    override val modifier: TastyModifier,
    private val verticalArrangement: TastyVerticalArrangement,
    private val horizontalAlignment: TastyHorizontalAlignment,
    private val scrollable: Boolean,
    val children: List<TastyView>
) : TastyView {

    override actual fun render(): TastyPlatformView {
        return TastyPlatformView {
            var nativeModifier = modifier.toAndroidModifier()

            val inheritedScrollState = LocalTastyScrollState.current
            if (scrollable && inheritedScrollState == null) {
                nativeModifier = nativeModifier.verticalScroll(rememberScrollState())
            }

            val composeArrangement = when (verticalArrangement) {
                is TastyVerticalArrangement.Top -> Arrangement.Top
                is TastyVerticalArrangement.Bottom -> Arrangement.Bottom
                is TastyVerticalArrangement.Center -> Arrangement.Center
                is TastyVerticalArrangement.SpaceBetween -> Arrangement.SpaceBetween
                is TastyVerticalArrangement.SpaceAround -> Arrangement.SpaceAround
                is TastyVerticalArrangement.SpaceEvenly -> Arrangement.SpaceEvenly

                is TastyVerticalArrangement.SpacedBy -> Arrangement.spacedBy(verticalArrangement.spaceDp.dp)
            }

            val composeAlignment = when (horizontalAlignment) {
                TastyHorizontalAlignment.Start -> Alignment.Start
                TastyHorizontalAlignment.Center -> Alignment.CenterHorizontally
                TastyHorizontalAlignment.End -> Alignment.End
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