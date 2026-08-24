package org.beem.tastymap.ui.detailsheet.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.core.paging.TastyPagingState
import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.ui.detailsheet.components.ReviewItemCard
import org.beem.tastymap.ui.theme.AppColors

fun LazyListScope.reviewsTabSection(
    pagingState: TastyPagingState<ReviewItem>,
    fontFamily: FontFamily
) {
    // 1. Değerlendirmeler Başlığı & Sayaç
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Değerlendirmeler & Yorumlar",
                fontFamily = fontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
            if (pagingState.items.isNotEmpty()) {
                Surface(
                    color = AppColors.SurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${pagingState.items.size} Yorum",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.NavyBlue
                    )
                }
            }
        }
    }

    // 2. Boş Durum
    if (pagingState.items.isEmpty() && !pagingState.isLoading) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Henüz bir değerlendirme bulunmuyor. İlk yorumu sen yap!",
                    fontFamily = fontFamily,
                    color = AppColors.TextTertiary,
                    fontSize = 14.sp
                )
            }
        }
    } else {
        // 3. Yorum Listesi
        items(
            items = pagingState.items,
            key = { it.id }
        ) { review ->
            ReviewItemCard(review = review)
        }
    }

    // 4. Sayfalama / Yükleme Animasyonu
    if (pagingState.isLoading) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AppColors.GourmetOrange,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}