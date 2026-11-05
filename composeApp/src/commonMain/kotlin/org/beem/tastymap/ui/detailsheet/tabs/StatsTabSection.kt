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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.core.util.toHighlightUiModels
import org.beem.tastymap.place.model.details.PlaceDetailsResult
import org.beem.tastymap.review.model.ScoreType
import org.beem.tastymap.ui.components.TastyProgressBar
import org.beem.tastymap.ui.theme.AppColors

fun LazyListScope.statsTabSection(
    details: PlaceDetailsResult?,
    fontFamily: FontFamily
) {
    val stats = details?.stats
    val totalReviews = stats?.totalReviewCount ?: 0

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
                        tint = AppColors.NavySoft,
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
                // Sol: Tipografik ve Asil Skor
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(0.38f)
                ) {
                    Text(
                        text = "${stats?.overallRating ?: 0.0}",
                        fontFamily = fontFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary // Aşırı turuncu yerine tok koyu renk
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
                                tint = if (index < starScore.toInt()) AppColors.Gold else AppColors.BorderStrong.copy(alpha = 0.5f),
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

                // Sağ: 5'ten 1'e Yıldız Dağılım Barları (Sakin NavySoft)
                Column(
                    modifier = Modifier.weight(0.62f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (5 downTo 1).forEach { star ->
                        val count = stats?.starDistribution?.get(star) ?: 0
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
                            TastyProgressBar(
                                progress = progress,
                                height = 4.dp,
                                activeColor = AppColors.NavySoft,
                                trackColor = AppColors.BorderLight,
                                modifier = Modifier.weight(1f)
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

    // 2. Öne Çıkan Özellikler Rozetleri (GourmetOrange'ın parladığı mikro vurgu)
    if (stats != null && stats.highlights.isNotEmpty()) {
        item {
            val highlightUiModels = remember(stats.highlights) {
                stats.highlights.toHighlightUiModels()
            }

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

                // Taşan rozetleri otomatik alt satıra geçiren düzen
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    highlightUiModels.forEach { badge ->
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
                                    text = badge.content,
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
                        TastyProgressBar(
                            progress = (score / 5.0f),
                            height = 4.dp,
                            activeColor = AppColors.NavySoft,
                            trackColor = AppColors.BorderLight,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}