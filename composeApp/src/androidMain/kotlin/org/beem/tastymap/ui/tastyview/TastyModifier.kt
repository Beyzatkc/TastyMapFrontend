package org.beem.tastymap.ui.tastyview


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun TastyModifier.toAndroidModifier(): Modifier {
    var m: Modifier = Modifier


    if (this.marginTop > 0 || this.marginBottom > 0) {
        m = m.padding(top = this.marginTop.dp, bottom = this.marginBottom.dp)
    }

    if (this.fillMaxWidth) m = m.fillMaxWidth()
    this.widthPx?.let { m = m.width(it.dp) }
    this.heightPx?.let { m = m.height(it.dp) }


    if (this.backgroundColor != null) {
        try {
            m = m.background(color = backgroundColor!!.toComposeColor() , shape = RoundedCornerShape(this.borderRadius.dp))
        } catch (_: Exception) {}
    }

    if (this.padding > 0 || this.paddingTop > 0 || this.paddingBottom > 0) {
        m = m.padding(
            top = if (this.paddingTop > 0) this.paddingTop.dp else this.padding.dp,
            bottom = if (this.paddingBottom > 0) this.paddingBottom.dp else this.padding.dp,
            start = this.padding.dp,
            end = this.padding.dp
        )
    }


    return m
}