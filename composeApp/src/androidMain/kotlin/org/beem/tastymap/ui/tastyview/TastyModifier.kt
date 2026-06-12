package org.beem.tastymap.ui.tastyview


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun TastyModifier.toAndroidModifier(): Modifier {
    var composeModifier: Modifier = Modifier

    // 1. Genişlik Ayarları
    if (this.fillMaxWidth) {
        composeModifier = composeModifier.fillMaxWidth()
    }

    // Web'deki "px" veya "100%" string değerlerini Android için güvenli dp'ye çeviriyoruz
    this.width?.let { w ->
        val cleanW = w.replace("px", "").toIntOrNull()
        if (cleanW != null) composeModifier = composeModifier.width(cleanW.dp)
    }
    this.height?.let { h ->
        val cleanH = h.replace("px", "").toIntOrNull()
        if (cleanH != null) composeModifier = composeModifier.height(cleanH.dp)
    }

    // 2. Padding ve Margin Ayarları
    // Android Compose'da harici bir "Margin" bileşeni yoktur; padding zincirinin sırası margin görevi görür.
    // Önce dış boşlukları (Margin) ekiyoruz:
    if (this.marginTop > 0 || this.marginBottom > 0) {
        composeModifier = composeModifier.padding(
            top = this.marginTop.dp,
            bottom = this.marginBottom.dp
        )
    }

    // 3. Arka Plan ve Oval Köşeler
    if (this.backgroundColor != null) {
        try {
            val parsedColor = Color(android.graphics.Color.parseColor(this.backgroundColor))
            composeModifier = composeModifier.background(
                color = parsedColor,
                shape = RoundedCornerShape(this.borderRadius.dp)
            )
        } catch (_: Exception) {}
    }

    // 4. İç Boşluk (Gerçek Padding)
    if (this.padding > 0 || this.paddingBottom > 0) {
        composeModifier = composeModifier.padding(
            top = this.padding.dp,
            start = this.padding.dp,
            end = this.padding.dp,
            bottom = if (this.paddingBottom > 0) SouthPadding(this.paddingBottom) else this.padding.dp
        )
    }

    return composeModifier
}

// Yardımcı fonksiyon: Sadece alttan gelen özel padding eklemesi varsa korusun
private fun SouthPadding(bottom: Int): androidx.compose.ui.unit.Dp = bottom.dp