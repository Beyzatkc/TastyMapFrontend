package org.beem.tastymap.ui.tastyview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.beem.tastymap.ui.tastyview.contractual.TastyStickyScrollableContent


actual class TastyStickyContainer actual constructor(
    override val modifier: TastyModifier,
    private val stickyHeader: TastyView,
    private val scrollableContent: TastyStickyScrollableContent
): TastyView{
    actual override fun render(): TastyPlatformView {
        return TastyPlatformView{
            val scrollPixelHolder = remember { mutableStateOf(0) }

            LaunchedEffect(Unit) {
                currentTriggerPixelOffset.value = 0
            }


            val isDividerPassed by remember {
                derivedStateOf {
                    val scrollPosition = scrollPixelHolder.value
                    val triggerTarget = currentTriggerPixelOffset.value

                    val shouldOpen = triggerTarget > 0 && scrollPosition >= (triggerTarget)

                    println("--- TastySticky Canlı Takip ---")
                    println("Scroll: $scrollPosition | Hedef Piksel: $triggerTarget | Durum: $shouldOpen")

                    shouldOpen
                }
            }

            BoxWithConstraints(
                modifier = modifier.toAndroidModifier().fillMaxSize()
            ) {
                val availableMaxHeight = maxHeight
                Box(modifier = Modifier.fillMaxWidth()
                    .height(availableMaxHeight)
                ) {
                    CompositionLocalProvider(
                        LocalTastyScrollState provides scrollPixelHolder,
                        LocalIsPagingActive provides false
                    ) {
                        scrollableContent.render().content()
                    }
                }

                AnimatedVisibility(
                    visible = isDividerPassed,
                    enter = fadeIn(animationSpec = tween(durationMillis = 250)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 250)),
                    modifier = Modifier.align(Alignment.TopCenter)
                        .fillMaxWidth()
                ) {
                    stickyHeader.render().content()
                }
            }

        }
    }

}