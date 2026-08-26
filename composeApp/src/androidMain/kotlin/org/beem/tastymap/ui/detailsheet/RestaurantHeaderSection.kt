package org.beem.tastymap.ui.detailsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@Composable
fun RestaurantHeaderSection(
    restaurant: Restaurant,
    tastyMapRating: Double? = null,
    tastyMapReviewCount: Int? = null
) {
    val fontFamily = getAppFontFamily()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Satır: Mekan Adı ve Puan Rozetleri
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = restaurant.name,
                fontFamily = fontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Puan Rozetleri (Yan yana TastyMap + Google)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. TastyMap Rozeti (Doğal ve Net Vurgulu)
                Surface(
                    color = AppColors.GourmetOrange.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "TastyMap Puanı",
                            tint = AppColors.Gold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (tastyMapRating != null) "$tastyMapRating" else "-",
                            fontFamily = fontFamily,
                            color = AppColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Tasty",
                            fontFamily = fontFamily,
                            color = AppColors.GourmetOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // 2. Google Rozeti (Dengeli, Temiz Nötr Zemin)
                Surface(
                    color = AppColors.SurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Google Puanı",
                            tint = AppColors.Gold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (restaurant.rating != null) "${restaurant.rating}" else "-",
                            fontFamily = fontFamily,
                            color = AppColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Google",
                            fontFamily = fontFamily,
                            color = AppColors.NavySoft,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 2. Satır: Kategori, Durum ve Yorum Sayısı Bilgileri
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (restaurant.category.isNotBlank()) {
                Surface(
                    color = AppColors.SurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = restaurant.category.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.NavySoft
                    )
                }
            }

            if (restaurant.status.isNotBlank()) {
                val isOpen = restaurant.status == "OPERATIONAL"
                val statusColor = if (isOpen) AppColors.SuccessGreen else AppColors.ErrorRed

                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isOpen) "Açık" else "Kapalı",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            // Toplam TastyMap Değerlendirme Sayısı
            if (tastyMapReviewCount != null && tastyMapReviewCount > 0) {
                Text(
                    text = "•  $tastyMapReviewCount Tasty Değerlendirmesi",
                    fontFamily = fontFamily,
                    fontSize = 12.sp,
                    color = AppColors.TextTertiary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 3. Satır: Adres
        if (restaurant.address.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Adres",
                    tint = AppColors.TextTertiary,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = restaurant.address,
                    fontFamily = fontFamily,
                    fontSize = 13.sp,
                    color = AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}