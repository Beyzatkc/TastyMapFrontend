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

    // 🎯 1. ADIM: ÖNCE DIŞ BOŞLUKLAR (MARGIN)
    // Compose'da background'dan önce eklenen padding 'Margin' görevi görür.
    // Arka planın dışındaki boşluğu garantiye alıyoruz.
    if (this.marginTop > 0 || this.marginBottom > 0) {
        composeModifier = composeModifier.padding(
            top = this.marginTop.dp,
            bottom = this.marginBottom.dp
        )
    }

    // 🎯 2. ADIM: GENİŞLİK VE YÜKSEKLİK AYARLARI
    // Sınırları çiziyoruz ki alt bileşenler ekranı delip geçmesin.
    if (this.fillMaxWidth) {
        composeModifier = composeModifier.fillMaxWidth()
    }

    this.width?.let { w ->
        val cleanW = w.replace("px", "").toIntOrNull()
        if (cleanW != null) composeModifier = composeModifier.width(cleanW.dp)
    }
    this.height?.let { h ->
        val cleanH = h.replace("px", "").toIntOrNull()
        if (cleanH != null) composeModifier = composeModifier.height(cleanH.dp)
    }

    // 🎯 3. ADIM: ARKA PLAN VE OVAL KÖŞELER
    // Tam ölçülerin üzerine ve dış boşluğun (margin) içerisine arka plan rengini giydiriyoruz.
    if (this.backgroundColor != null) {
        try {
            val parsedColor = Color(android.graphics.Color.parseColor(this.backgroundColor))
            composeModifier = composeModifier.background(
                color = parsedColor,
                shape = RoundedCornerShape(this.borderRadius.dp)
            )
        } catch (_: Exception) {}
    }

    // 🎯 4. ADIM: İÇ BOŞLUK (GERÇEK PADDING)
    // Arka plandan SONRA eklenen padding gerçek iç boşluktur.
    // İçerideki text veya çocuk elemanlar bu sınıra çarpıp içeri bükülür, taşma yapmaz!
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

// Senin o asil yardımcı fonksiyonun aynen kalıyor dayıcım
private fun SouthPadding(bottom: Int): androidx.compose.ui.unit.Dp = bottom.dp