package org.beem.tastymap.ui.map.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailSheet(
    state: RestaurantDetailState,
    onAction: (RestaurantAction) -> Unit,
    onDismiss: () -> Unit
) {
    println("dış ${state.restaurant}")
    if (state.restaurant != null) {
        println(state.restaurant)
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                RestaurantHeaderSection(state.restaurant, state.isFavorite) {
                    onAction(RestaurantAction.ToggleFavorite)
                }

                RestaurantInfoSection(state.restaurant)

                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }
}