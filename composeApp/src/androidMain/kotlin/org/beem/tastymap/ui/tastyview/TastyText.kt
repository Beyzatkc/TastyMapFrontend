package org.beem.tastymap.ui.tastyview

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

actual class TastyText actual constructor(
    private val text: String,
    private val style: TastyTextStyle,
    private val color: String?,
    private val maxLines: Int,
    private val onOverflow: (()-> Unit)?,
    override val modifier: TastyModifier
) : TastyView {

    override actual fun render(): TastyPlatformView {
        val composeColor = color?.toComposeColor()
            ?: when (style) {
                TastyTextStyle.TITLE -> Color.Black
                TastyTextStyle.SUBTITLE -> Color.Black
                TastyTextStyle.BODY -> Color.Black
                TastyTextStyle.BADGE -> Color.Black
                else -> Color.Black
            }

        return TastyPlatformView {
            val (fontSize, fontWeight, letterSpacing, lineHeight) = when (style) {
                TastyTextStyle.TITLE -> Quadruple(24.sp, FontWeight.ExtraBold, (-0.5).sp, 30.sp)
                TastyTextStyle.SUBTITLE -> Quadruple(17.sp, FontWeight.Bold, (-0.2).sp, 22.sp)
                TastyTextStyle.BODY -> Quadruple(14.sp, FontWeight.Normal, 0.sp, 20.sp)
                TastyTextStyle.BADGE -> Quadruple(11.sp, FontWeight.SemiBold, 0.5.sp, 14.sp)
            }

            Text(
                text = text,
                modifier = modifier.toAndroidModifier(),
                color = composeColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                letterSpacing = letterSpacing,
                lineHeight = lineHeight,
                maxLines = maxLines,
                fontFamily = FontFamily.SansSerif,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult ->
                    if (textLayoutResult.hasVisualOverflow){
                        onOverflow?.invoke()
                    }
                }
            )
        }
    }
}