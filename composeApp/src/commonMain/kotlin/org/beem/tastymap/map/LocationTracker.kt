package org.beem.tastymap.map

import kotlinx.coroutines.flow.StateFlow
import org.beem.tastymap.data.model.LocationData

interface LocationTracker {
    val locationState: StateFlow<LocationData>

    fun startTracking()

    fun stopTracking()
}