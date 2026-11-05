package org.beem.tastymap.ui.tastyview


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp

fun TastyModifier.toAndroidModifier(): Modifier {
    var m: Modifier = Modifier

    if (this.fillMaxWidth) m = m.fillMaxWidth()
    this.widthPx?.let { m = m.width(it.dp) }
    this.heightPx?.let { m = m.height(it.dp) }

    if (this.marginTop > 0 || this.marginBottom > 0) {
        m = m.padding(top = this.marginTop.dp, bottom = this.marginBottom.dp)
    }



    if (this.backgroundColor != null) {
        try {
            m = m.background(color = backgroundColor!!.toComposeColor() , shape = RoundedCornerShape(this.borderRadius.dp))
        } catch (_: Exception) {}
    }

    if (this.padding > 0 || this.paddingTop > 0 || this.paddingBottom > 0) {
        m = m.padding(
            top = if (this.paddingTop > 0) this.paddingTop.dp else this.padding.dp,
            bottom = if (this.paddingBottom > 0) this.paddingBottom.dp else this.padding.dp,
            start = if (this.paddingLeft > 0) this.paddingLeft.dp else padding.dp,
            end = if (this.paddingRight > 0) this.paddingRight.dp else padding.dp
        )
    }

    if(this.isStickyTrigger){
        m = m.onGloballyPositioned{ coordinates ->
            val coordinates = coordinates.positionInParent().y.toInt()
            if(coordinates>0 && currentTriggerPixelOffset.value == 0){
                currentTriggerPixelOffset.value = coordinates
            }
        }
    }

    if (this.onClick != null) {
        m = m.clickable { this.onClick?.invoke() }
    }


    return m
}