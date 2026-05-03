package org.beem.tastymap.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.beem.tastymap.map.MapScreenModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailsSheet(screenModel: MapScreenModel) {
    val restaurant by screenModel.selectedRestaurant.collectAsState()
    val isVisible by screenModel.showDetails.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = { screenModel.closeDetails() },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            restaurant?.let { res ->
                // Burası senin tasarımın
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text(res.name, style = MaterialTheme.typography.headlineMedium)
                    Text("Konya, Türkiye", style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = {  }, modifier = Modifier.fillMaxWidth()) {
                        Text("Git")
                    }
                }
            }
        }
    }
}