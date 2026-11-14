package org.beem.tastymap.ui.ai.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.ai.model.AiQuotaState
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily


@Composable
fun HeaderQuotaDetailPanel(
    quotaState: AiQuotaState,
    customColors: CustomColors,
    modifier: Modifier = Modifier
) {
    val fontFamily = getAppFontFamily()

    val barColor = when {
        !quotaState.hasQuota -> customColors.error
        quotaState.isLowQuota -> customColors.warning
        else -> customColors.gourmetOrange
    }

    val animatedProgress by animateFloatAsState(
        targetValue = quotaState.remainingProgress,
        animationSpec = tween(450, easing = FastOutSlowInEasing)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(start = 12.dp, end = 0.dp, top = 5.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (quotaState.hasQuota) {
                    "${formatTokenCount(quotaState.remainingTokens)} token kaldı"
                } else {
                    "Günlük limit doldu"
                },
                fontFamily = fontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (quotaState.hasQuota) customColors.textPrimary else customColors.error
            )

            Text(
                text = "/ ${formatTokenCount(quotaState.dailyLimit)}",
                fontFamily = fontFamily,
                fontSize = 11.sp,
                color = customColors.textSecondary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(customColors.borderStrong.copy(alpha = 0.4f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(barColor)
            )
        }
    }
}

private fun formatTokenCount(value: Int): String {
    return value.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}