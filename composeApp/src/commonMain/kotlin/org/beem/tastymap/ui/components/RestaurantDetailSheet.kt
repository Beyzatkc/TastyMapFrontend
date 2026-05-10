package org.beem.tastymap.ui.components

import androidx.compose.runtime.Composable
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.map.bottomsheet.RestaurantAction

@Composable
expect fun RestaurantDetailSheet(
    restaurant: Restaurant,
    backgroundColor: String = "#FFFFFF",
    primaryColor: String = "#00008B",
    cornerRadius: Int = 24,
    sheetWidthPercentage: Int = 90,
    onAction: (RestaurantAction) -> Unit,
    onDismiss: () -> Unit
)