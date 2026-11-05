package org.beem.tastymap.ui.detailsheet.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.beem.tastymap.core.util.toFormatTimestamp
import org.beem.tastymap.place.model.MapReviewSource
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.review.model.ScoreType
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReviewItemCard(
    review: ReviewItem,
    modifier: Modifier = Modifier
) {
    val fontFamily = getAppFontFamily()
    var isExpanded by remember { mutableStateOf(false) }

    val initials = remember(review.name) {
        val parts = review.name.trim().split(" ").filter { it.isNotBlank() }
        when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.isNotEmpty() && parts[0].isNotEmpty() -> parts[0].take(2).uppercase()
            else -> "TM"
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        border = BorderStroke(1.dp, AppColors.BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Üst Satır: Avatar, İsim, Rozet, Beğeni ve Puan Kutucuğu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sol Bölüm: Avatar + İsim + Tarih
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    if (!review.userProfile.isNullOrBlank()) {
                        AsyncImage(
                            model = review.userProfile,
                            contentDescription = review.name,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AppColors.SurfaceVariant),
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.Medium
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (review.source == MapReviewSource.INTERNAL)
                                        AppColors.GourmetOrange.copy(alpha = 0.14f)
                                    else
                                        AppColors.SurfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (review.source == MapReviewSource.INTERNAL)
                                    AppColors.GourmetOrange
                                else
                                    AppColors.TextSecondary
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = review.name.ifBlank { "Anonim Gurme" },
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AppColors.TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Kaynak Rozeti (HeaderSection ile birebir aynı)
                            if (review.source == MapReviewSource.INTERNAL) {
                                Surface(
                                    color = AppColors.GourmetOrange.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "TastyMap",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontFamily = fontFamily,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.GourmetOrange
                                    )
                                }
                            } else {
                                Surface(
                                    color = AppColors.SurfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Google",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontFamily = fontFamily,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.NavySoft
                                    )
                                }
                            }
                        }

                        Text(
                            text = review.createdAt.toFormatTimestamp(),
                            fontFamily = fontFamily,
                            fontSize = 11.sp,
                            color = AppColors.TextTertiary
                        )
                    }
                }

                // Sağ Bölüm: Beğeni ve Puan Rozeti
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (review.likeCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Beğeni Sayısı",
                                tint = AppColors.ErrorRed,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = review.likeCount.toString(),
                                fontFamily = fontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.TextSecondary
                            )
                        }
                    }

                    // Puan Rozeti (UserOwnReviewCard / HeaderSection tarzında Gold + TextPrimary)
                    Surface(
                        color = if (review.source == MapReviewSource.INTERNAL)
                            AppColors.GourmetOrange.copy(alpha = 0.14f)
                        else
                            AppColors.SurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AppColors.Gold,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${review.rating}",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AppColors.TextPrimary
                            )
                        }
                    }
                }
            }

            // 2. Yorum Metni
            if (!review.content.isNullOrBlank()) {
                Text(
                    text = review.content,
                    fontFamily = fontFamily,
                    fontSize = 13.sp,
                    color = AppColors.TextSecondary,
                    lineHeight = 19.sp,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isExpanded = !isExpanded
                    }
                )
            }

            // 3. Alt Skor Rozetleri (Lezzet, Hijyen, Hız...)
            val subScores = remember(review.scores) {
                review.scores.filter { it.type != ScoreType.OVERALL }
            }
            if (subScores.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    subScores.forEach { scoreItem ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AppColors.SurfaceVariant,
                            border = BorderStroke(0.5.dp, AppColors.BorderLight)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = scoreItem.type.toDisplayName(),
                                    fontFamily = fontFamily,
                                    fontSize = 11.sp,
                                    color = AppColors.TextSecondary
                                )
                                Text(
                                    text = "${scoreItem.score}",
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = AppColors.NavySoft
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun ScoreType.toDisplayName(): String = when (this) {
    ScoreType.OVERALL -> "Genel"
    ScoreType.TASTE -> "Lezzet"
    ScoreType.WAITING_TIME -> "Hız"
    ScoreType.SERVICE -> "Servis"
    ScoreType.HOSPITALITY -> "İlgi"
    ScoreType.PRICE_PERFORMANCE -> "F/P"
    ScoreType.CLEANLINESS -> "Hijyen"
    ScoreType.LOCATION -> "Konum"
    ScoreType.INTERIOR_DESIGN -> "Mekan"
}