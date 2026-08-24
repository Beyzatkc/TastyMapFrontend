package org.beem.tastymap.ui.detailsheet.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.place.model.details.PlaceDetailsResult
import org.beem.tastymap.review.model.ScoreType
import org.beem.tastymap.ui.theme.AppColors

fun LazyListScope.statsTabSection(
    details: PlaceDetailsResult?,
    fontFamily: FontFamily
) {
    val stats = details?.stats
    val totalReviews = stats?.totalReviewCount ?: 0

    // Henüz hiç TastyMap değerlendirmesi yoksa
    if (totalReviews == 0) {
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppColors.SurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = AppColors.GourmetOrange,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Henüz İstatistik Oluşmadı",
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "Bu mekan için ilk değerlendirmeyi yaparak topluluk istatistiklerini başlatabilirsin!",
                        fontFamily = fontFamily,
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
        return
    }

    // 1. Genel Puan & Yıldız Dağılımı Kartı
    item {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AppColors.SurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sol: Büyük Ortalama Skoru
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(0.38f)
                ) {
                    Text(
                        text = "${stats?.overallRating}",
                        fontFamily = fontFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.GourmetOrange
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            val starScore = stats?.overallRating ?: 0.0
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = if (index < starScore.toInt()) AppColors.GourmetOrange else AppColors.BorderLight,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = "$totalReviews değerlendirme",
                        fontFamily = fontFamily,
                        fontSize = 11.sp,
                        color = AppColors.TextTertiary
                    )
                }

                // Sağ: 5'ten 1'e Yıldız Dağılım Barları
                Column(
                    modifier = Modifier.weight(0.62f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (5 downTo 1).forEach { star ->
                        val count = stats?.starDistribution[star] ?: 0
                        val progress = if (totalReviews > 0) count.toFloat() / totalReviews else 0f

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "$star",
                                fontFamily = fontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextSecondary
                            )
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = AppColors.GourmetOrange,
                                trackColor = AppColors.BorderLight
                            )
                            Text(
                                text = "$count",
                                fontFamily = fontFamily,
                                fontSize = 11.sp,
                                color = AppColors.TextTertiary,
                                modifier = Modifier.width(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // 2. Öne Çıkan Özellikler Rozetleri (Highlights)
    if (stats != null && stats.highlights.isNotEmpty()) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Öne Çıkan Özellikler",
                    fontFamily = fontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    stats.highlights.forEach { badge ->
                        Surface(
                            color = AppColors.GourmetOrange.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = AppColors.GourmetOrange,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = badge,
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.GourmetOrange
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 3. Kriter Bazlı Dağılım Çubukları
    val criteriaList = stats?.criteriaMetrics?.filter { it.type != ScoreType.OVERALL } ?: emptyList()
    if (criteriaList.isNotEmpty()) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Kriter Değerlendirmeleri",
                    fontFamily = fontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )

                criteriaList.forEach { metric ->
                    val displayName = metric.type.displayName
                    val score = metric.averageScore.toFloat()

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = displayName,
                                    fontFamily = fontFamily,
                                    fontSize = 13.sp,
                                    color = AppColors.TextPrimary
                                )
                                Text(
                                    text = "(${metric.count} oy)",
                                    fontFamily = fontFamily,
                                    fontSize = 11.sp,
                                    color = AppColors.TextTertiary
                                )
                            }
                            Text(
                                text = "${metric.averageScore} / 5.0",
                                fontFamily = fontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.NavySoft
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (score / 5.0f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = AppColors.GourmetOrange,
                            trackColor = AppColors.BorderLight
                        )
                    }
                }
            }
        }
    }
}