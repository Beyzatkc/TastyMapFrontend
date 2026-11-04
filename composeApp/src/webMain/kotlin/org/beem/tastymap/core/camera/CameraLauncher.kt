package org.beem.tastymap.core.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.FileReader
import org.w3c.files.get
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

actual class CameraLauncher(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        onLaunch()
    }
}

@Composable
actual fun rememberCameraLauncher(onResult: (ByteArray?) -> Unit): CameraLauncher {
    return remember {
        CameraLauncher {
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.accept = "image/*"
            input.setAttribute("capture", "environment")

            input.onchange = { _: Event ->
                val file = input.files?.get(0)
                if (file != null) {
                    val reader = FileReader()
                    reader.onload = {
                        val arrayBuffer = reader.result as ArrayBuffer
                        val uint8Array = Uint8Array(arrayBuffer)
                        val bytes = ByteArray(uint8Array.length) { i -> uint8Array[i] }
                        onResult(bytes)
                    }
                    reader.readAsArrayBuffer(file)
                } else {
                    onResult(null)
                }
            }

            input.click()
        }
    }
}