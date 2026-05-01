package org.beem.tastymap

import androidx.compose.ui.window.ComposeUIViewController
import org.beem.tastymap.core.di.appModule
import org.beem.tastymap.di.iosModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule, iosModule)
    }
}
fun MainViewController() = ComposeUIViewController { App() }