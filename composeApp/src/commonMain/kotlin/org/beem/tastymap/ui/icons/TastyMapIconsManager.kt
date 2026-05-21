package org.beem.tastymap.ui.icons

import androidx.compose.runtime.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import tastymap.composeapp.generated.resources.Res

object TastyMapIconsManager {

    private var _loadedIcons = mutableStateOf<Map<TastyMapIcons, String>>(emptyMap())
    val loadedIcons: State<Map<TastyMapIcons, String>> = _loadedIcons

    private var isPreloadingStarted = false

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun initialize() {
        if (isPreloadingStarted) return
        isPreloadingStarted = true

        LaunchedEffect(Unit) {
            val tempMap = mutableMapOf<TastyMapIcons, String>()
            try {
                TastyMapIcons.values().forEach { icon ->
                    val rawBytes = Res.readBytes(icon.resPath)
                    tempMap[icon] = rawBytes.decodeToString()
                }
                _loadedIcons.value = tempMap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun getHtmlIcon(icon: TastyMapIcons): String {
        val rawSvg = _loadedIcons.value[icon] ?: return ""
        return icon.toHtmlEmbedded(rawSvg)
    }
}