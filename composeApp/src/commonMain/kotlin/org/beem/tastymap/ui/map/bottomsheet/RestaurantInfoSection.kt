package org.beem.tastymap.ui.map.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.components.InfoRow

@Composable
fun RestaurantInfoSection(restaurant: Restaurant) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        InfoRow(icon = Icons.Default.LocationOn, text = restaurant.rating.toString())
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            icon = Icons.Default.Info,
            text = if (restaurant.name == "OPERATIONAL") "Şuan Açık" else "Kapalı",
            textColor = if (restaurant.name == "OPERATIONAL") Color(0xFF4CAF50) else Color.Red
        )
    }
}