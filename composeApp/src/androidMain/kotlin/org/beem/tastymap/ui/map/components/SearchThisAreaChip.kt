package org.beem.tastymap.ui.map.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@Composable
actual fun SearchThisAreaChip(
    visible: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val fontFamily = getAppFontFamily()

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { -it },
        exit = fadeOut() + slideOutVertically { -it },
        modifier = modifier
    ) {
        Surface(
            onClick = onClick,
            enabled = !isLoading,
            shape = RoundedCornerShape(24.dp),
            color = AppColors.Surface,
            shadowElevation = 6.dp,
            border = BorderStroke(1.dp, AppColors.BorderLight)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = AppColors.GourmetOrange
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Alanı Yenile",
                        tint = AppColors.GourmetOrange,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = if (isLoading) "Mekanlar Aranıyor..." else "Bu Bölgede Ara",
                    fontFamily = fontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            }
        }
    }
}