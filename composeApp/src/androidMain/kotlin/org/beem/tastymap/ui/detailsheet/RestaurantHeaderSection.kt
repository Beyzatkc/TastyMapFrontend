package org.beem.tastymap.ui.detailsheet

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.place.state.PlaceDetailsUiState
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@Composable
fun RestaurantHeaderSection(
    restaurant: Restaurant,
    detailsUiState: PlaceDetailsUiState,
    isSaved: Boolean = false,
    onSaveClick: () -> Unit = {},
    onDirectionsClick: () -> Unit = {}
) {
    val fontFamily = getAppFontFamily()
    val details = detailsUiState.details

    // Backend detay geldiyse onu, henüz gelmediyse harita marker'ından gelen ilk veriyi kullan
    val displayName = details?.name ?: restaurant.name
    val displayAddress = details?.formattedAddress ?: restaurant.address
    val tastyRating = details?.tastyMapRating
    val tastyReviewCount = details?.tastyMapReviewCount
    val googleRating = details?.googleRating ?: restaurant.rating

    // Kategori formatlama
    val primaryCategory = details?.types?.firstOrNull { it != "point_of_interest" && it != "establishment" }
        ?.replace("_", " ")
        ?.replaceFirstChar { it.uppercase() }
        ?: restaurant.category.replaceFirstChar { it.uppercase() }

    val saveBgColor by animateColorAsState(
        if (isSaved) AppColors.GourmetOrange.copy(alpha = 0.14f) else AppColors.SurfaceVariant
    )
    val saveIconColor by animateColorAsState(
        if (isSaved) AppColors.GourmetOrange else AppColors.TextSecondary
    )

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
                text = displayName,
                fontFamily = fontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Puan Rozetleri
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TastyMap Rozeti
                Surface(
                    color = AppColors.GourmetOrange.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "TastyMap Puanı",
                            tint = AppColors.Gold,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (tastyRating != null && tastyRating > 0.0) "$tastyRating" else "-",
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
                            fontSize = 10.sp
                        )
                    }
                }

                // Google Rozeti
                Surface(
                    color = AppColors.SurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Google Puanı",
                            tint = AppColors.Gold,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (googleRating != null && googleRating > 0.0) "$googleRating" else "-",
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
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // 2. Satır: Kategori + Yorum Sayısı (Solda) & Kompakt Aksiyon Butonları (Sağda)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol: Kategori & Yorum Sayısı
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (primaryCategory.isNotBlank()) {
                    Surface(
                        color = AppColors.SurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = primaryCategory,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontFamily = fontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.NavySoft
                        )
                    }
                }

                if (tastyReviewCount != null && tastyReviewCount > 0) {
                    Text(
                        text = "• $tastyReviewCount yorum",
                        fontFamily = fontFamily,
                        fontSize = 11.sp,
                        color = AppColors.TextTertiary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Sağ: Kompakt Aksiyonlar (Yol Tarifi + Kaydet)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Kompakt Yol Tarifi Butonu
                Surface(
                    onClick = onDirectionsClick,
                    shape = RoundedCornerShape(8.dp),
                    color = AppColors.NavyBlue
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Directions,
                            contentDescription = "Yol Tarifi",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Yol Tarifi",
                            fontFamily = fontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Kompakt Kaydet Butonu
                Surface(
                    onClick = onSaveClick,
                    shape = RoundedCornerShape(8.dp),
                    color = saveBgColor
                ) {
                    Box(
                        modifier = Modifier.size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isSaved) "Kaydedildi" else "Kaydet",
                            tint = saveIconColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 3. Satır: Adres
        if (displayAddress.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 1.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Adres",
                    tint = AppColors.TextTertiary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = displayAddress,
                    fontFamily = fontFamily,
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}