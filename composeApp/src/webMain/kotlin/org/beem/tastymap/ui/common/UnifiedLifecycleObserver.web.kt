package org.beem.tastymap.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Document
import org.w3c.dom.events.Event

// 1. Wasm'da eksik olan visibilityState özelliğini okumak için bu fonksiyonu tanımlıyoruz:
@JsFun("(document) => document.visibilityState")
private external fun getVisibilityState(document: Document): String

@Composable
actual fun UnifiedLifecycleObserver(
    onActive: () -> Unit,
    onInactive: () -> Unit
) {
    DisposableEffect(Unit) {
        val visibilityListener: (Event) -> Unit = {
            // 2. document.visibilityState yerine oluşturduğumuz fonksiyonu kullanıyoruz
            if (getVisibilityState(document) == "visible") onActive() else onInactive()
        }
        val focusListener: (Event) -> Unit = { onActive() }
        val blurListener: (Event) -> Unit = { onInactive() }

        document.addEventListener("visibilitychange", visibilityListener)
        window.addEventListener("focus", focusListener)
        window.addEventListener("blur", blurListener)

        onActive()

        onDispose {
            document.removeEventListener("visibilitychange", visibilityListener)
            window.removeEventListener("focus", focusListener)
            window.removeEventListener("blur", blurListener)
            onInactive()
        }
    }
}