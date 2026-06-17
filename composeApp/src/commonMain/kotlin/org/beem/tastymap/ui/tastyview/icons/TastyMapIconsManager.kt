package org.beem.tastymap.ui.tastyview.icons

import androidx.compose.runtime.*
import org.beem.tastymap.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

object TastyMapIconsManager {
    private var _loadedIcons = mutableStateOf<Map<TastyMapIcon, String>>(emptyMap())

    var isReady = mutableStateOf(false)
        private set

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun initialize() {
        LaunchedEffect(Unit) {
            if (isReady.value) return@LaunchedEffect
            val tempMap = mutableMapOf<TastyMapIcon, String>()
            try {
                TastyMapIcon.values().forEach { icon ->
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

    fun getRawSvg(icon: TastyMapIcon): String {
        return _loadedIcons.value[icon] ?: ""
    }
}