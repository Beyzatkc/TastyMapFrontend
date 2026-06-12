package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

actual class TastySpacer actual constructor(
    private val sizePx: Int,
) : TastyView {

    override actual fun render(): TastyPlatformView {
        return TastyPlatformView {
            Spacer(
                modifier = if (true) {
                    Modifier.width(sizePx.dp)
                } else {
                    Modifier.height(sizePx.dp)
                }
            )
        }
    }
}