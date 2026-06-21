
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
                TastyBoxAlignment.TopStart -> Alignment.TopStart
                TastyBoxAlignment.TopCenter -> Alignment.TopCenter
                TastyBoxAlignment.TopEnd -> Alignment.TopEnd
                TastyBoxAlignment.CenterStart -> Alignment.CenterStart
                TastyBoxAlignment.Center -> Alignment.Center
                TastyBoxAlignment.CenterEnd -> Alignment.CenterEnd
                TastyBoxAlignment.BottomStart -> Alignment.BottomStart
                TastyBoxAlignment.BottomCenter -> Alignment.BottomCenter
                TastyBoxAlignment.BottomEnd -> Alignment.BottomEnd
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