package org.beem.tastymap.ui.detailsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
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


@Composable
fun RestaurantHeaderSection(restaurant: Restaurant) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = restaurant.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AppColors.NavyBlue,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Puan Rozeti
            Surface(
                color = AppColors.NavyBlue,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Puan",
                        tint = AppColors.Gold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (restaurant.rating != null && restaurant.rating!! > 0.0) restaurant.rating.toString() else "-",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Kategori & Durum Etiketleri
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (restaurant.category.isNotBlank()) {
                Surface(
                    color = AppColors.WarmAmber.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = restaurant.category.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.WarmAmber
                    )
                }
            }

            if (restaurant.status.isNotBlank()) {
                Surface(
                    color = if (restaurant.status == "OPERATIONAL") AppColors.EmeraldAccent.copy(alpha = 0.15f) else AppColors.passwordRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (restaurant.status == "OPERATIONAL") "Açık" else "Kapalı",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (restaurant.status == "OPERATIONAL") AppColors.EmeraldAccent else AppColors.passwordRed
                    )
                }
            }
        }

        if (restaurant.address.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Adres",
                    tint = AppColors.DarkGrayLines,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = restaurant.address,
                    fontSize = 13.sp,
                    color = AppColors.DarkGrayLines,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}