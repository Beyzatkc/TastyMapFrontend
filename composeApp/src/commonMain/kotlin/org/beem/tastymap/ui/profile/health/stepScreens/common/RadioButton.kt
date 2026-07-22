package org.beem.tastymap.ui.profile.health.stepScreens.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.theme.CustomColors


@Composable
 fun RadioButton(
    text: String,
    selected: Boolean,
    customColors: CustomColors,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) customColors.gold else customColors.lineAlpha.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label = "BorderColorAnimation"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) customColors.gold.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = tween(200),
        label = "BackgroundColorAnimation"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) customColors.navy else customColors.lineAlpha.copy(alpha = 1f),
        animationSpec = tween(200),
        label = "TextColorAnimation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = customColors.gold,
                unselectedColor = customColors.lineAlpha.copy(alpha = 0.6f),
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}