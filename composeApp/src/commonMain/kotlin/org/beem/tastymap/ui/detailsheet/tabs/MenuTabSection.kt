package org.beem.tastymap.ui.detailsheet.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.theme.AppColors

fun LazyListScope.menuTabSection(
    fontFamily: FontFamily
) {
    val highlights = listOf(
        Triple("Özel Trüflü Burger", "Karamelize soğan, trüf mayonez, 180g dana köfte", "4.9"),
        Triple("Çıtır Patates Sepeti", "Özel baharat karışımı ve cheddar sos ile", "4.7"),
        Triple("Ev Yapımı Limonata", "Taze nane ve zencefil dokunuşu ile", "4.6")
    )

    item {
        Text(
            text = "Topluluğun Önerdiği İmza Lezzetler",
            fontFamily = fontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }

    highlights.forEach { (title, desc, score) ->
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = AppColors.SurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = title,
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            text = desc,
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            color = AppColors.TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.GourmetOrange.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ThumbUp,
                                contentDescription = null,
                                tint = AppColors.GourmetOrange,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = score,
                                fontFamily = fontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.GourmetOrange
                            )
                        }
                    }
                }
            }
        }
    }
}