package org.beem.tastymap.ui.detailsheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.place.model.review.UserReviewSummaryDto
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@Composable
fun UserOwnReviewCard(
    review: UserReviewSummaryDto,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamily = getAppFontFamily()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.SurfaceVariant
        ),
        border = BorderStroke(1.dp, AppColors.BorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Üst Satır: Başlık, Puan Rozeti ve Düzenle Butonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Senin Değerlendirmen",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AppColors.TextPrimary
                    )

                    // TastyMap Gurme Turuncusu Puan Rozeti
                    Surface(
                        color = AppColors.Surface, // Kart içi ferah beyaz zemin
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AppColors.BorderLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AppColors.Gold,
                                modifier = Modifier.size(13.dp)
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

                // Modern, tıklanabilirliği belirginleştirilmiş Düzenle Butonu
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppColors.NavySoft.copy(alpha = 0.08f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Düzenle",
                        tint = AppColors.NavySoft,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Yorum Metni
            review.text?.takeIf { it.isNotBlank() }?.let { reviewText ->
                Text(
                    text = reviewText,
                    fontFamily = fontFamily,
                    fontSize = 13.sp,
                    color = AppColors.TextSecondary,
                    lineHeight = 19.sp
                )
            }
        }
    }
}