package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.beem.tastymap.data.model.ai.HydratedPlaceCardDto
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.theme.CustomColors

@Composable
fun HydratedPlaceCard(
    place: HydratedPlaceCardDto,
    customColors: CustomColors,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val isClickable = place.dbId != null && place.isAvailable

    Surface(
        modifier = modifier
            .width(230.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = isClickable) {
                place.dbId?.let { onIntent(AiChatIntent.PlaceClicked(it)) }
            },
        color = customColors.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Mekan Adı ve Canlı Google Puanı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = customColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (place.googleRating != null && place.googleRating > 0.0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = place.googleRating.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = customColors.textPrimary
                        )
                    }
                }
            }

            // Canlı Adres / Semt
            place.vicinity?.let { address ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textSecondary.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // AI'ın Sohbet Özelinde Önerdiği Lezzetler
            if (place.highlightedFoods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    place.highlightedFoods.take(2).forEach { food ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(customColors.gourmetOrange.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = food,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = customColors.gourmetOrange
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Canlı Durum (Açık/Kapalı)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val (statusText, statusColor) = when {
                    !place.isAvailable -> "Ulaşılamıyor" to customColors.textSecondary
                    place.isOpenNow == true -> "Açık" to Color(0xFF2E7D32)
                    place.isOpenNow == false -> "Kapalı" to Color(0xFFD32F2F)
                    else -> "Bilinmiyor" to customColors.textSecondary
                }

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall.copy(color = statusColor)
                )
            }
        }
    }
}