package org.beem.tastymap.ui.detailsheet.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun PeekHeaderSection(
    restaurant: Restaurant,
    detailsUiState: PlaceDetailsUiState,
    isSaved: Boolean = false,
    onCloseClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit = {}
) {
    val fontFamily = getAppFontFamily()
    val details = detailsUiState.details

    val displayName = details?.name ?: restaurant.name
    val tastyRating = details?.tastyMapRating
    val googleRating = details?.googleRating ?: restaurant.rating
    val primaryCategory = details?.types?.firstOrNull { it != "point_of_interest" && it != "establishment" }
        ?.replace("_", " ")
        ?.replaceFirstChar { it.uppercase() }
        ?: restaurant.category.replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Üst Satır: Mekan İsmi + Kapat Butonu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontFamily = fontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    if (primaryCategory.isNotBlank()) {
                        Text(
                            text = primaryCategory,
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.TextSecondary
                        )
                        Text(text = "•", color = AppColors.TextTertiary, fontSize = 11.sp)
                    }

                    // Minimal Puanlar (Yan Yana)
                    if (tastyRating != null && tastyRating > 0.0) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = AppColors.Gold, modifier = Modifier.size(11.dp))
                            Text(text = "$tastyRating", fontFamily = fontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppColors.GourmetOrange)
                        }
                    }

                    if (googleRating != null && googleRating > 0.0) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = AppColors.Gold, modifier = Modifier.size(11.dp))
                            Text(text = "$googleRating", fontFamily = fontFamily, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                        }
                    }
                }
            }

            // Minimal Kapat Butonu
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.size(28.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = AppColors.SurfaceVariant,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kapat",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }

        // Alt Satır: Google Maps Tarzı Hızlı Aksiyon Hapları (Chips)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Yol Tarifi (Vurgulu Mavi Buton)
            Button(
                onClick = onDirectionsClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.NavyBlue),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Rounded.Directions, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Yol Tarifi", fontFamily = fontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Kaydet Butonu (Hap)
            OutlinedButton(
                onClick = onSaveClick,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(0.8.dp, AppColors.BorderLight),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = if (isSaved) AppColors.GourmetOrange else AppColors.TextPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isSaved) "Kaydedildi" else "Kaydet",
                    fontFamily = fontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextPrimary
                )
            }

            // Paylaş Butonu (Hap)
            OutlinedButton(
                onClick = onShareClick,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(0.8.dp, AppColors.BorderLight),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Outlined.Share, contentDescription = null, tint = AppColors.TextPrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Paylaş", fontFamily = fontFamily, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
            }
        }
    }
}