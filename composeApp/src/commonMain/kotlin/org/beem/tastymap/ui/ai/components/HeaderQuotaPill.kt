package org.beem.tastymap.ui.ai.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.ai.model.AiQuotaState
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@Composable
fun HeaderQuotaPill(
    quotaState: AiQuotaState,
    isExpanded: Boolean,
    customColors: CustomColors,
    onToggle: () -> Unit
) {
    val fontFamily = getAppFontFamily()

    val ringColor by animateColorAsState(
        targetValue = when {
            !quotaState.hasQuota -> customColors.error
            quotaState.isLowQuota -> customColors.warning
            else -> customColors.gourmetOrange
        },
        animationSpec = tween(300)
    )

    val animatedProgress by animateFloatAsState(
        targetValue = quotaState.remainingProgress,
        animationSpec = tween(450, easing = FastOutSlowInEasing)
    )

    val trackColor = customColors.borderStrong.copy(alpha = 0.45f)

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onToggle)
            .padding(start = 8.dp, end = 0.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Canlı Dairesel Doluluk Halkası
        Canvas(modifier = Modifier.size(15.dp)) {
            val strokeWidth = 2.2.dp.toPx()
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "%${quotaState.remainingPercentage.toInt()}",
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = customColors.textPrimary
        )

        Spacer(modifier = Modifier.width(2.dp))

        Icon(
            imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
            contentDescription = "Kota Detayı",
            tint = customColors.textSecondary,
            modifier = Modifier.size(14.dp)
        )
    }
}
