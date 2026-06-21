// androidMain / org.beem.tastymap.ui.tastyview / TastyRow.kt
package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalArrangement
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalAlignment

actual class TastyRow actual constructor(
    override val modifier: TastyModifier,
    private val horizontalArrangement: TastyHorizontalArrangement,
    private val verticalAlignment: TastyVerticalAlignment,
    private val children: List<TastyView>
) : TastyView {

    override actual fun render(): TastyPlatformView {
        return TastyPlatformView {
            val nativeModifier = modifier.toAndroidModifier()
            val composeArrangement = when (horizontalArrangement) {
                is TastyHorizontalArrangement.Start -> Arrangement.Start
                is TastyHorizontalArrangement.End -> Arrangement.End
                is TastyHorizontalArrangement.Center -> Arrangement.Center
                is TastyHorizontalArrangement.SpaceBetween -> Arrangement.SpaceBetween
                is TastyHorizontalArrangement.SpaceAround -> Arrangement.SpaceAround
                is TastyHorizontalArrangement.SpaceEvenly -> Arrangement.SpaceEvenly

                is TastyHorizontalArrangement.SpacedBy -> Arrangement.spacedBy(horizontalArrangement.spaceDp.dp)
            }

            val composeAlignment = when (verticalAlignment) {
                TastyVerticalAlignment.Top -> Alignment.Top
                TastyVerticalAlignment.Center -> Alignment.CenterVertically
                TastyVerticalAlignment.Bottom -> Alignment.Bottom
            }

            Row(
                modifier = nativeModifier, // Bizim dolgularımız ve genişliğimiz burada
                horizontalArrangement = composeArrangement,
                verticalAlignment = composeAlignment
            ) {
                children.forEach { child ->
                    val childWeight = child.modifier.weight
                    if (childWeight != null && childWeight > 0f) {
                        // İç sarmalayıcı Row'un ana hizalamayı bozmaması için koruma
                        Box(
                            modifier = Modifier.weight(childWeight, fill = true),
                            contentAlignment = when(composeAlignment) {
                                Alignment.Top -> Alignment.TopStart
                                Alignment.CenterVertically -> Alignment.CenterStart
                                Alignment.Bottom -> Alignment.BottomStart
                                else -> Alignment.CenterStart
                            }
                        ) {
                            child.render().content()
                        }
                    } else {
                        // 🎯 İşte o asil dokunuş: Çocukları doğrudan basıyoruz,
                        // sarmalayıcı eklemediğimiz için Row'un SpaceBetween kuralı
                        // ana Row'un padding sınırlarına çarparak durmak zorunda kalıyor!
                        child.render().content()
                    }
                }
            }

            /*
            Row(
                modifier = nativeModifier,
                horizontalArrangement = composeArrangement,
                verticalAlignment = composeAlignment
            ) {
                children.forEach { child ->
                    val childWeight = child.modifier.weight
                    if(childWeight != null && childWeight > 0f) {
                        Row(
                            modifier = Modifier.weight(childWeight, fill = true),
                            verticalAlignment = composeAlignment,
                        ) {
                            child.render().content()
                        }
                    }
                    else {
                        child.render().content()
                    }
                }
            }

             */
        }
    }

}