package org.beem.tastymap

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import org.beem.tastymap.core.di.appModule
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.di.webModule
import org.koin.core.context.startKoin

private external val window: dynamic



@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(appModule, webModule)
    }

    window.onpopstate = {
        DeepLinkManager.handleLink(window.location.href)
    }
    ComposeViewport {
        App()
    }
}