package org.beem.tastymap.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.resources.ExperimentalResourceApi
import tastymap.composeapp.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
@Composable
fun rememberCentralizedSvg(resourcePath: String, targetColor: String): String {
    var svgText by remember(resourcePath, targetColor) { mutableStateOf("") }

    LaunchedEffect(resourcePath, targetColor) {
        try {
            val rawBytes = Res.readBytes(resourcePath)
            val decodedSvg = rawBytes.decodeToString()

            var processedSvg = decodedSvg
                .replace(Regex("""stroke="[^"]*""""), """stroke="$targetColor"""")
                .replace(Regex("""fill="[^"]*""""), """fill="$targetColor"""")

            processedSvg = processedSvg
                .replace(Regex("""width="[^"]*""""), """width="100%"""")
                .replace(Regex("""height="[^"]*""""), """height="100%"""")

            svgText = processedSvg
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return svgText
}