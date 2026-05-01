package org.beem.tastymap.map

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import org.beem.tastymap.data.model.LocationData
import platform.Foundation.NSURL
import platform.CoreGraphics.CGRectZero

@Composable
actual fun TastyMapComponent(
    modifier: Modifier,
    mapUrl: String,
    userLocation: LocationData,
    state: TastyMapState
) {
    /*
    val styleNSURL = NSURL.URLWithString(mapUrl)
    UIKitView(
        modifier = modifier,
        factory = {
            val mapView =
        }
    )

     */

    Box(modifier = modifier)
}