package org.beem.tastymap.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.beem.tastymap.data.model.LocationData


@Composable
expect fun TastyMapComponent(
    modifier: Modifier = Modifier,
    mapUrl: String,
    userLocation: LocationData,
    state: TastyMapState
)