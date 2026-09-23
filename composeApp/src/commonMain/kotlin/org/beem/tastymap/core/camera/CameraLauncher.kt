package org.beem.tastymap.core.camera

import androidx.compose.runtime.Composable

expect class CameraLauncher {
    fun launch()
}

@Composable
expect fun rememberCameraLauncher(onResult: (ByteArray?) -> Unit): CameraLauncher