package org.beem.tastymap.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.beem.tastymap.data.model.LocationData
import org.beem.tastymap.ui.map.states.CameraPositionState


@Composable
expect fun TastyMapComponent(
    modifier: Modifier = Modifier,
    mapUrl: String,
    userLocation: LocationData,
    state: TastyMapState,
    onFabClicked: () -> Unit
)