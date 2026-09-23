package org.beem.tastymap.core.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.getBytes
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

actual class CameraLauncher(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        onLaunch()
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberCameraLauncher(onResult: (ByteArray?) -> Unit): CameraLauncher {
    val delegate = remember {
        object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                val nsData = image?.let { UIImageJPEGRepresentation(it, 0.9) }
                val bytes = nsData?.toByteArray()

                picker.dismissViewControllerAnimated(true, null)
                onResult(bytes)
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, null)
                onResult(null)
            }
        }
    }

    return remember {
        CameraLauncher {
            val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController
            if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
                val picker = UIImagePickerController().apply {
                    sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                    this.delegate = delegate
                }
                rootController?.presentViewController(picker, animated = true, completion = null)
            } else {
                onResult(null)
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val bytes = ByteArray(this.length.toInt())
    if (bytes.isNotEmpty()) {
        this.getBytes(bytes.usePinned { it.addressOf(0) }, this.length)
    }
    return bytes
}