package org.beem.tastymap.ui.map.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.beem.tastymap.map.model.ActiveRouteState

@Composable
expect fun ActiveRouteBottomCard(
    routeState: ActiveRouteState,
    onCloseRoute: () -> Unit,
    modifier: Modifier = Modifier
)