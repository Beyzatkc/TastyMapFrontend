package org.beem.tastymap

import androidx.compose.ui.window.ComposeUIViewController
import org.beem.tastymap.core.di.appModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}
fun MainViewController() = ComposeUIViewController { App() }