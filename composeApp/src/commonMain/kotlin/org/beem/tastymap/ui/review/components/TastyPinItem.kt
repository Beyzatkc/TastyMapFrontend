package org.beem.tastymap.ui.review.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.theme.AppColors


@Composable
fun TastyPinItem(
    fillFraction: Float,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (fillFraction > 0f) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "pinScale"
    )


    Box(
        modifier = modifier
            .size(44.dp)
            .scale(animatedScale),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = TastyPinIcon,
            contentDescription = null,
            tint = AppColors.DarkGrayLines.copy(alpha = 0.18f),
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                }
        )

        if (fillFraction > 0f) {
            Icon(
                imageVector = TastyPinIcon,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    .drawWithContent {
                        clipRect(
                            left = 0f,
                            top = 0f,
                            right = size.width * fillFraction,
                            bottom = size.height
                        ) {
                            this@drawWithContent.drawContent()
                        }
                    }
            )
        }
    }
}