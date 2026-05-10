package org.beem.tastymap.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.map.bottomsheet.RestaurantAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun RestaurantDetailSheet(
    restaurant: Restaurant,
    backgroundColor: String,
    primaryColor: String,
    cornerRadius: Int,
    sheetWidthPercentage: Int,
    onAction: (RestaurantAction) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(restaurant.name, fontWeight = FontWeight.Bold)
            Text(text = restaurant.rating.toString())

            Button(
                onClick = { onAction(RestaurantAction.GetDirections) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Yol Tarifi Al")
            }
        }
    }
}