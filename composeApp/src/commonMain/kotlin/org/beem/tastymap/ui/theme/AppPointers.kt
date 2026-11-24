package org.beem.tastymap.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

/**
 * Tema renklerine (fill ve stroke) göre dinamik yüksek kontrastlı I-beam imleci yapılandırır.
 */
expect fun createHighContrastTextPointerIcon(
    fillColor: Color,
    strokeColor: Color
): PointerIcon

@Immutable
data class AppPointers(
    val text: PointerIcon,
    val clickable: PointerIcon = PointerIcon.Hand
)

val LocalAppPointers = staticCompositionLocalOf {
    AppPointers(text = PointerIcon.Text)
}

@Composable
fun Modifier.adaptiveTextPointer(): Modifier =
    this.pointerHoverIcon(LocalAppPointers.current.text)