package org.beem.tastymap.ui.detailsheet.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.theme.AppColors

@Composable
fun PlaceActionBar(
    placeId: String?,
    latitude: Double?,
    longitude: Double?,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onWriteReviewClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier
) {
    val saveBgColor by animateColorAsState(
        if (isSaved) AppColors.GourmetOrange.copy(alpha = 0.14f) else AppColors.SurfaceVariant
    )
    val saveIconColor by animateColorAsState(
        if (isSaved) AppColors.GourmetOrange else AppColors.TextSecondary
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Birincil Aksiyon: Yol Tarifi
        Surface(
            onClick = onDirectionsClick,
            modifier = Modifier
                .weight(1.2f)
                .height(42.dp),
            shape = RoundedCornerShape(10.dp),
            color = AppColors.NavyBlue
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Directions,
                    contentDescription = "Yol Tarifi Al",
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Yol Tarifi",
                    fontFamily = fontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // 2. İkincil Aksiyon: Değerlendir
        Surface(
            onClick = onWriteReviewClick,
            modifier = Modifier
                .weight(1.1f)
                .height(42.dp),
            shape = RoundedCornerShape(10.dp),
            color = AppColors.GourmetOrange.copy(alpha = 0.14f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.RateReview,
                    contentDescription = "Değerlendir",
                    tint = AppColors.GourmetOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Değerlendir",
                    fontFamily = fontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.GourmetOrange
                )
            }
        }

        // 3. Yardımcı Aksiyon: Kaydet / Koleksiyon
        Surface(
            onClick = onSaveClick,
            modifier = Modifier
                .size(42.dp),
            shape = RoundedCornerShape(10.dp),
            color = saveBgColor
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (isSaved) "Kaydedildi" else "Kaydet",
                    tint = saveIconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}