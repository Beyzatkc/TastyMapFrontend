package org.beem.tastymap.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.theme.AppColors

@Composable
fun TastyProgressBar(
    progress: Float, // 0.0f .. 1.0f
    modifier: Modifier = Modifier,
    height: Dp = 4.dp,
    activeColor: Color = AppColors.NavySoft,
    trackColor: Color = AppColors.BorderLight
) {
    val coercedProgress = progress.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = coercedProgress,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "tastyProgressBarAnim"
    )

    // Arka plan rayı (Track)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
    ) {
        // Yalnızca değer 0'dan büyükse aktif dolguyu çiz (Böylece 0 iken nokta oluşmaz)
        if (animatedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(height / 2))
                    .background(activeColor)
            )
        }
    }
}