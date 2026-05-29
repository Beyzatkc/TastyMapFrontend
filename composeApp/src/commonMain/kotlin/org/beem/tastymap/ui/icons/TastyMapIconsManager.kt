package org.beem.tastymap.ui.icons

import androidx.compose.runtime.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import tastymap.composeapp.generated.resources.Res

object TastyMapIconsManager {
    private var _loadedIcons = mutableStateOf<Map<TastyMapIcons, String>>(emptyMap())

    var isReady = mutableStateOf(false)
        private set

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun initialize() {
        LaunchedEffect(Unit) {
            if (isReady.value) return@LaunchedEffect
            val tempMap = mutableMapOf<TastyMapIcons, String>()
            try {
                TastyMapIcons.values().forEach { icon ->
                    val rawBytes = Res.readBytes(icon.resPath)
                    tempMap[icon] = rawBytes.decodeToString()
                }
                _loadedIcons.value = tempMap
                isReady.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRawSvg(icon: TastyMapIcons): String {
        return _loadedIcons.value[icon] ?: ""
    }
}