package org.beem.tastymap.ui.tastyview

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

actual class TastyText actual constructor(
    private val text: String,
    private val style: TastyTextStyle,
    private val color: String?,
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
            // 🎯 TASARIM DİLİ MAPPING'İ:
            // Web'deki o şık font büyüklüklerini ve kalınlıklarını
            // Android'in native tipografi standartlarına birebir eşitliyoruz.
            val (fontSize, fontWeight, letterSpacing) = when (style) {
                TastyTextStyle.TITLE -> Triple(22.sp, FontWeight.Bold, (-0.5).sp)
                TastyTextStyle.SUBTITLE -> Triple(16.sp, FontWeight.SemiBold, 0.sp)
                TastyTextStyle.BODY -> Triple(14.sp, FontWeight.Normal, 0.sp)
                TastyTextStyle.BADGE -> Triple(11.sp, FontWeight.Bold, 0.5.sp)
                else -> Triple(14.sp, FontWeight.Normal, 0.sp)
            }

            Text(
                text = text,
                color = composeColor!!,
                fontSize = fontSize,
                fontWeight = fontWeight,
                letterSpacing = letterSpacing,
                // Restoran adresleri veya uzun yorumlar taşarsa çirkin durmasın diye
                // jenerik olarak akıllı kırpma (Ellipsis) desteğini de buraya gömüyoruz dayıcım
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}