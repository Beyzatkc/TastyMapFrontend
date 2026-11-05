package org.beem.tastymap.ui.ai

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun AdjustResizeEffect() {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val activity = context as? Activity
        val window = activity?.window
        val originalMode = window?.attributes?.softInputMode

        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        onDispose {
            // Kullanıcı bu ekrandan çıktığında MainActivity'deki orijinal moda geri döner
            if (originalMode != null) {
                window.setSoftInputMode(originalMode)
            }
        }
    }
}