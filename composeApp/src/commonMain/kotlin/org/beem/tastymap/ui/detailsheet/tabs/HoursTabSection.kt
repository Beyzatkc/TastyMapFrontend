package org.beem.tastymap.ui.detailsheet.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.core.util.PlaceOpenStatus
import org.beem.tastymap.core.util.calculateTodayStatus
import org.beem.tastymap.core.util.getCurrentTurkishDay
import org.beem.tastymap.place.model.details.PlaceDetailsResult
import org.beem.tastymap.ui.theme.AppColors

fun LazyListScope.hoursTabSection(
    details: PlaceDetailsResult?,
    fontFamily: FontFamily
) {
    val openingHours = details?.openingHours
    val weekdayText = openingHours?.weekdayText.orEmpty()

    // 1. Veri Yoksa Durum Kartı
    if (weekdayText.isEmpty()) {
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppColors.SurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccessTime,
                        contentDescription = null,
                        tint = AppColors.NavySoft,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Çalışma Saatleri Belirtilmemiş",
                        fontFamily = fontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "Bu mekan için henüz çalışma saatleri girilmemiş.",
                        fontFamily = fontFamily,
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }
        }
        return
    }

    val (currentStatus, todayHoursText) = weekdayText.calculateTodayStatus()
    val currentDayName = getCurrentTurkishDay()

    item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 2. Dinamik Durum Kartı (Hesaplanan Açık/Kapalı + Tahmini Rozeti)
            val statusColor = when (currentStatus) {
                PlaceOpenStatus.OPEN -> AppColors.SuccessGreen
                PlaceOpenStatus.CLOSED -> AppColors.ErrorRed
                PlaceOpenStatus.UNKNOWN -> AppColors.TextSecondary
            }

            val statusIcon = when (currentStatus) {
                PlaceOpenStatus.OPEN -> Icons.Rounded.CheckCircle
                PlaceOpenStatus.CLOSED -> Icons.Rounded.RemoveCircle
                PlaceOpenStatus.UNKNOWN -> Icons.Rounded.HelpOutline
            }

            val statusTitle = when (currentStatus) {
                PlaceOpenStatus.OPEN -> "Şu Anda Açık Olabilir"
                PlaceOpenStatus.CLOSED -> "Şu Anda Kapalı Olabilir"
                PlaceOpenStatus.UNKNOWN -> "Çalışma Durumu Belirsiz"
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = statusColor.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = statusTitle,
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                        Text(
                            text = if (todayHoursText != null) "Bugün için belirtilen saat: $todayHoursText" else "Saat aralığı tespit edilemedi",
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            color = AppColors.TextSecondary
                        )
                    }
                }
            }

            // 3. Haftalık Çalışma Saatleri Kartı
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppColors.SurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    weekdayText.forEach { itemText ->
                        val parts = itemText.split(":", limit = 2)
                        val day = parts.getOrNull(0)?.trim().orEmpty()
                        val hours = parts.getOrNull(1)?.trim().orEmpty()

                        val isToday = day.equals(currentDayName, ignoreCase = true)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isToday) AppColors.NavySoft.copy(alpha = 0.08f) else androidx.compose.ui.graphics.Color.Transparent)
                                .padding(horizontal = if (isToday) 10.dp else 4.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isToday) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(AppColors.NavyBlue)
                                    )
                                }
                                Text(
                                    text = day,
                                    fontFamily = fontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isToday) AppColors.NavyBlue else AppColors.TextPrimary
                                )
                                if (isToday) {
                                    Text(
                                        text = "(Bugün)",
                                        fontFamily = fontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.NavySoft
                                    )
                                }
                            }

                            Text(
                                text = hours,
                                fontFamily = fontFamily,
                                fontSize = 13.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday) AppColors.NavyBlue else AppColors.TextSecondary
                            )
                        }
                    }
                }
            }

            // 4. Dürüstlük / Bilgilendirme Notu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = AppColors.TextTertiary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Çalışma saatleri resmi tatiller veya özel günlerde değişiklik gösterebilir.",
                    fontFamily = fontFamily,
                    fontSize = 11.sp,
                    color = AppColors.TextTertiary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}