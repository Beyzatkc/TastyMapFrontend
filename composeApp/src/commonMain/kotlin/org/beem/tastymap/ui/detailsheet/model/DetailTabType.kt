package org.beem.tastymap.ui.detailsheet.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.ui.graphics.vector.ImageVector

enum class DetailTabType(
    val title: String,
    val icon: ImageVector
) {
    REVIEWS(
        title = "Değerlendirmeler",
        icon = Icons.Rounded.RateReview
    ),
    STATS(
        title = "İstatistikler",
        icon = Icons.Rounded.BarChart
    ),
    HOURS(
        title = "Çalışma Saatleri",
        icon = Icons.Rounded.AccessTime
    ),
    MENU(
        title = "Öne Çıkanlar",
        icon = Icons.Rounded.RestaurantMenu
    )
}