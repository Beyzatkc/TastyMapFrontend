package org.beem.tastymap.ui.detailsheet

import androidx.compose.runtime.Composable
import org.beem.tastymap.data.model.Restaurant

@Composable
expect fun TastyDetailSheet(
    restaurant: Restaurant,
    onDirectionsClick: () -> Unit,
    onDismiss: () -> Unit
)