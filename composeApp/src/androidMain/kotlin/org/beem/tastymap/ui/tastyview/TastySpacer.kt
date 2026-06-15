package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

actual class TastySpacer actual constructor(
    override val modifier: TastyModifier
) : TastyView {

    override actual fun render(): TastyPlatformView {
        return TastyPlatformView {
            Spacer(
                modifier = modifier.toAndroidModifier()
            )
        }
    }
}