package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.core.util.LocationUtils
import org.beem.tastymap.data.model.ai.HydratedPlaceCardDto
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@Composable
fun PlacePreviewFloatingCard(
    place: HydratedPlaceCardDto,
    userLat: Double?,
    userLng: Double?,
    customColors: CustomColors,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamily = getAppFontFamily()
    val distanceText = LocationUtils.formatDistance(
        userLat = userLat,
        userLng = userLng,
        placeLat = place.latitude,
        placeLng = place.longitude
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 540.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = customColors.surface,
            border = BorderStroke(1.dp, customColors.borderLight),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // 1. Üst Satır: Başlık, Meta Bilgiler ve Kapat Butonu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = place.name,
                            fontFamily = fontFamily,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp,
                            color = customColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Puan • Durum • Mesafe • Semt
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (place.googleRating != null && place.googleRating > 0.0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = customColors.accentAmber,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = place.googleRating.toString(),
                                        fontFamily = fontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = customColors.textPrimary
                                    )
                                }
                                MetaDot(customColors)
                            }

                            if (place.isOpenNow != null) {
                                val statusColor = if (place.isOpenNow) {
                                    customColors.accentEmerald
                                } else {
                                    customColors.error
                                }
                                Text(
                                    text = if (place.isOpenNow) "Açık" else "Kapalı",
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = statusColor
                                )
                                if (distanceText != null || !place.vicinity.isNullOrBlank()) {
                                    MetaDot(customColors)
                                }
                            }

                            if (distanceText != null) {
                                Text(
                                    text = distanceText,
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = customColors.textSecondary
                                )
                                if (!place.vicinity.isNullOrBlank()) {
                                    MetaDot(customColors)
                                }
                            }

                            if (!place.vicinity.isNullOrBlank()) {
                                Text(
                                    text = place.vicinity,
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp,
                                    color = customColors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        onClick = { onIntent(AiChatIntent.DismissPlacePreview) },
                        shape = CircleShape,
                        color = customColors.surfaceVariant,
                        modifier = Modifier
                            .size(28.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Kapat",
                                tint = customColors.textSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                // 2. Vurgulanan Lezzetler (Yatay kaydırılabilir, taşma yapmaz)
                if (place.highlightedFoods.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        place.highlightedFoods.take(4).forEach { food ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(customColors.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = food,
                                    fontFamily = fontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = customColors.textSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Alt Eylem Satırı
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecondaryActionButton(
                        label = "Harita",
                        icon = Icons.Rounded.Map,
                        customColors = customColors,
                        onClick = { onIntent(AiChatIntent.ShowOnMapClicked(place)) },
                        modifier = Modifier.weight(1f)
                    )

                    SecondaryActionButton(
                        label = "Yol Tarifi",
                        icon = Icons.Rounded.NearMe,
                        customColors = customColors,
                        onClick = { onIntent(AiChatIntent.DirectionsClicked(place)) },
                        modifier = Modifier.weight(1f)
                    )

                    if (place.dbId != null) {
                        Surface(
                            onClick = { onIntent(AiChatIntent.PlaceClicked(place.dbId)) },
                            shape = RoundedCornerShape(12.dp),
                            color = customColors.gourmetOrange,
                            modifier = Modifier
                                .weight(1.1f)
                                .height(38.dp)
                                .pointerHoverIcon(PointerIcon.Hand)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "İncele",
                                    fontFamily = fontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SecondaryActionButton(
    label: String,
    icon: ImageVector,
    customColors: CustomColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamily = getAppFontFamily()

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = customColors.surfaceVariant,
        modifier = modifier
            .height(38.dp)
            .pointerHoverIcon(PointerIcon.Hand)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = customColors.textPrimary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                fontFamily = fontFamily,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = customColors.textPrimary,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MetaDot(customColors: CustomColors) {
    Text(
        text = "•",
        fontSize = 11.sp,
        color = customColors.textSecondary.copy(alpha = 0.45f)
    )
}