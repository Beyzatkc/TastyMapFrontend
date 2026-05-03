package org.beem.tastymap

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.beem.tastymap.core.di.appModule
import org.beem.tastymap.di.webModule
import org.koin.core.context.GlobalContext.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Koin Başlatıcısı
    startKoin {
        modules(appModule, webModule)
    }

    // Hedef div'i bul
    val composeTarget = document.getElementById("compose-target")

    if (composeTarget != null) {
        // Hedefi göstererek ComposeViewport'u başlat
        ComposeViewport(composeTarget) {
            App()
        }
    } else {
        println("HATA: compose-target elementi bulunamadı!")
    }
}