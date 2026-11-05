package org.beem.tastymap.map

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.beem.tastymap.data.model.LocationData

class AndroidLocationTracker (
    private val context: Context
) : LocationTracker {

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)
    private val _locationState = MutableStateFlow(LocationData(0.0, 0.0))
    override val locationState: StateFlow<LocationData> = _locationState.asStateFlow()

    private val trackerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var simulationJob: Job? = null

    @SuppressLint("MissingPermission")
    override fun startTracking() {
        // Gerçek GPS dinlemesi yerine simülasyonu başlatıyoruz
        startBearingSimulation()

        /* Gerçek servisi açmak istediğinde burayı açarsın:
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
        locationClient.requestLocationUpdates(request, callBack, context.mainLooper)
        */
    }

    private fun startBearingSimulation() {
        if (simulationJob?.isActive == true) return

        simulationJob = trackerScope.launch {
            // Test Senaryosu: Açı listesi (0° Kuzey, 90° Doğu, 180° Güney, 270° Batı vb.)
            // Paint'te çizdiğin radar konisinin her yöne doğru nasıl döndüğünü bu sayede net göreceksin
            // 45f, 90f, 135f, 180f, 225f, 270f, 315f
            val testBearings = listOf(90f, 360f, 135f,45f, 180f, 0f, 225f, 270f, 315f)

            var baseLat = 37.946978
            var baseLng = 32.519940
            var index = 0

            while (isActive) {
                val currentBearing = testBearings[index]

                // Konumu hafifçe hareket ettir ki animasyon (ValueAnimator) smooth çalışsın
                baseLat += 0.0001
                baseLng += 0.0001

                val simulatedData = LocationData(
                    latitude = baseLat,
                    longitude = baseLng,
                    accuracy = 3f,
                    isAvailable = true,
                    bearing = currentBearing // <-- Test ettiğimiz açı
                )

                println("TastyMap Simulation -> Yön Değiştirildi: $currentBearing° (Lat: $baseLat, Lng: $baseLng)")
                _locationState.value = simulatedData

                // Her 2 saniyede bir bir sonraki açıya ve konuma geç
                delay(2000L)

                index = (index + 1) % testBearings.size
            }
        }
    }

    override fun stopTracking() {
        simulationJob?.cancel()
        simulationJob = null
    }
}