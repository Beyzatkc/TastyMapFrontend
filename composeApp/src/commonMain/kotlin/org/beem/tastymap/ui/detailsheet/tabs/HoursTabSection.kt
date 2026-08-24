package org.beem.tastymap.ui.detailsheet.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.place.model.details.PlaceDetailsResult
import org.beem.tastymap.ui.theme.AppColors

fun LazyListScope.hoursTabSection(
    details: PlaceDetailsResult?,
    fontFamily: FontFamily
) {
    val weekDays = listOf(
        "Pazartesi" to "09:00 - 23:00",
        "Salı" to "09:00 - 23:00",
        "Çarşamba" to "09:00 - 23:00",
        "Perşembe" to "09:00 - 23:00",
        "Cuma" to "09:00 - 00:00",
        "Cumartesi" to "10:00 - 00:00",
        "Pazar" to "10:00 - 22:30"
    )

    item {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AppColors.SurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Durum Rozeti
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(AppColors.SuccessGreen, CircleShape)
                    )
                    Text(
                        text = "Şu Anda Açık",
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.SuccessGreen
                    )
                }

                HorizontalDivider(color = AppColors.BorderLight, thickness = 1.dp)

                // Günlük Saat Listesi
                weekDays.forEachIndexed { index, (day, hours) ->
                    val isToday = index == 0 // Mock olarak Pazartesi aktif
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = day,
                            fontFamily = fontFamily,
                            fontSize = 13.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                            color = if (isToday) AppColors.GourmetOrange else AppColors.TextPrimary
                        )
                        Text(
                            text = hours,
                            fontFamily = fontFamily,
                            fontSize = 13.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) AppColors.GourmetOrange else AppColors.TextSecondary
                        )
                    }
                }
            }
        }
    }
}