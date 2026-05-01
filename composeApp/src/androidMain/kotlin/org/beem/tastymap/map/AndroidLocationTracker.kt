package org.beem.tastymap.map

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.beem.tastymap.data.model.LocationData

class AndroidLocationTracker (
    private val context: Context
) : LocationTracker{

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    private val _locationState = MutableStateFlow(LocationData(0.0, 0.0))


    override val locationState: StateFlow<LocationData> = _locationState.asStateFlow()

    @SuppressLint("MissingPermission")
    override fun startTracking() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        val callBack = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    _locationState.value = LocationData(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        accuracy = it.accuracy,
                        isAvailable = true,
                        bearing = it.bearing
                    )
                }
            }
        }
        locationClient.requestLocationUpdates(
            request,
            callBack,
            context.mainLooper
        )
    }

    override fun stopTracking() {
        TODO("Not yet implemented")
    }

}