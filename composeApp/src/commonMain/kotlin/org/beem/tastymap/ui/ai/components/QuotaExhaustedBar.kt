package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.LockClock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@Composable
fun QuotaExhaustedBar(
    dailyLimit: Int,
    customColors: CustomColors,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamily = getAppFontFamily()
    val formattedLimit = dailyLimit.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Sol Kilit/Süre İkonu
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(customColors.error.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.LockClock,
                contentDescription = null,
                tint = customColors.error,
                modifier = Modifier.size(19.dp)
            )
        }

        // Orta Açıklama Metni
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Günlük kullanım limitine ulaştınız",
                fontFamily = fontFamily,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = customColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$formattedLimit token limitiniz yarın sıfırlanır.",
                fontFamily = fontFamily,
                fontSize = 11.5.sp,
                color = customColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Sağ "Planı Yükselt" Hap Butonu
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(customColors.gourmetOrange)
                .clickable(onClick = onUpgradeClick)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Planı Yükselt",
                fontFamily = fontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}