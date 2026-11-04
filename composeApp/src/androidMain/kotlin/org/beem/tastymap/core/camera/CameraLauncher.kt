package org.beem.tastymap.core.camera

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.ByteArrayOutputStream

actual class CameraLauncher(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        onLaunch()
    }
}

@Composable
actual fun rememberCameraLauncher(onResult: (ByteArray?) -> Unit): CameraLauncher {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            onResult(stream.toByteArray())
        } else {
            onResult(null)
        }
    }

    return remember {
        CameraLauncher { launcher.launch(null) }
    }
}