package org.beem.tastymap.ui.tastyview


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

actual class TastyCard actual constructor(
    private val backgroundColor: String,
    private val cornerRadius: Int,
    private val padding: Int,
    private val children: List<TastyView>
) : TastyView {

    override actual fun render(): TastyPlatformView {
        val parseColor = { hex: String ->
            try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { Color.White }
        }
        val composeBgColor = parseColor(backgroundColor)

        return TastyPlatformView {
            // Web'deki genişlik, padding ve arka plan zekasını toAndroidModifier hallediyor zaten
            val nativeCardModifier = Modifier
            // 1. Önce arka plan rengini ve yuvarlak köşelerini basıyoruz
            .background(
                color = composeBgColor,
                shape = RoundedCornerShape(cornerRadius.dp)
            )
            // 2. Arka plandan SONRA yazılan padding gerçek İÇ BOŞLUKTUR.
            // Kartın içindeki yazılar bu sınıra çarpıp içeri bükülür, taşma yapmaz!
            .padding(padding.dp)

            Column(
                modifier = nativeCardModifier
            ) {
                children.forEach { child ->
                    child.render().content()
                }
            }
        }
    }
}