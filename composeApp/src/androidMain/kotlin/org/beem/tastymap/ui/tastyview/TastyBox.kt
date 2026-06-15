
package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyBoxAlignment

actual class TastyBox actual constructor(
    override val modifier: TastyModifier,
    private val contentAlignment: TastyBoxAlignment,
    private val children: List<TastyView>
) : TastyView {

    override actual fun render(): TastyPlatformView {
        return TastyPlatformView {
            val nativeModifier = modifier.toAndroidModifier()

            val composeAlignment = when (contentAlignment) {
                TastyBoxAlignment.TOP_START -> Alignment.TopStart
                TastyBoxAlignment.TOP_CENTER -> Alignment.TopCenter
                TastyBoxAlignment.TOP_END -> Alignment.TopEnd
                TastyBoxAlignment.CENTER_START -> Alignment.CenterStart
                TastyBoxAlignment.CENTER -> Alignment.Center
                TastyBoxAlignment.CENTER_END -> Alignment.CenterEnd
                TastyBoxAlignment.BOTTOM_START -> Alignment.BottomStart
                TastyBoxAlignment.BOTTOM_CENTER -> Alignment.BottomCenter
                TastyBoxAlignment.BOTTOM_END -> Alignment.BottomEnd
            }


            Box(
                modifier = nativeModifier,
                contentAlignment = composeAlignment
            ) {
                children.forEach { child ->
                    child.render().content()
                }
            }
        }
    }
}