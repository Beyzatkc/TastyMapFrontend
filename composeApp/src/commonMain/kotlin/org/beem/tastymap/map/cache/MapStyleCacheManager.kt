package org.beem.tastymap.map.cache

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.beem.tastymap.core.provider.DispatcherProvider


class MapStyleCacheManager(
    private val storage: MapStyleStorage,
    private val dispatcher: DispatcherProvider
) {
    private val mapClient by lazy { HttpClient() }

    private var memoryCache: Pair<String, String>? = null
    private val mutex = Mutex()
    private val backgroundScope = CoroutineScope(SupervisorJob() + dispatcher.io)

    /**
     * Harita stil JSON'ını en hızlı katmandan getirir:
     * 1. RAM Cache (< 1 ms)
     * 2. Disk Cache (< 5 ms) -> Bulursa anında döner, arka planda günceller
     * 3. Network (Sadece ilk kurulumda)
     */
    suspend fun getStyleJson(mapUrl: String): Result<String> {
        // 1. RAM kontrolü
        memoryCache?.let { (cachedUrl, cachedJson) ->
            if (cachedUrl == mapUrl && cachedJson.isNotBlank()) {
                return Result.success(cachedJson)
            }
        }

        return withContext(dispatcher.io){
            mutex.withLock {
                // Lock beklerken başka bir coroutine doldurmuş olabilir
                memoryCache?.let { (cachedUrl, cachedJson) ->
                    if (cachedUrl == mapUrl && cachedJson.isNotBlank()) {
                        return@withLock Result.success(cachedJson)
                    }
                }

                // 2. Disk kontrolü
                val diskJson = storage.getCachedStyle(mapUrl)
                if (!diskJson.isNullOrBlank()) {
                    memoryCache = mapUrl to diskJson
                    // Arka planda MapTiler'dan yeni stil var mı diye sessizce güncelle
                    refreshInBackground(mapUrl)
                    return@withLock Result.success(diskJson)
                }

                // 3. İlk açılış: Ağdan çek, RAM'e ve Diske yaz
                fetchAndSave(mapUrl)
            }
        }
    }

    /**
     * Splash ekranında çağrılarak harita açılmadan önce stili hazır eder.
     */
    fun prefetch(mapUrl: String) {
        backgroundScope.launch {
            getStyleJson(mapUrl)
        }
    }

    private fun refreshInBackground(mapUrl: String) {
        backgroundScope.launch {
            fetchAndSave(mapUrl)
        }
    }


    private suspend fun fetchAndSave(mapUrl: String): Result<String> = runCatching {
        val responseText = mapClient.get(mapUrl).bodyAsText()
        require(responseText.trimStart().startsWith("{") && responseText.contains("\"layers\"")) {
            "Geçersiz MapTiler Style JSON yanıtı"
        }
        memoryCache = mapUrl to responseText
        storage.saveStyle(mapUrl, responseText)
        responseText
    }
}