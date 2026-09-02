package org.beem.tastymap.core.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import kotlinx.browser.document

@Composable
actual fun ChangeAppLanguage(languageCode: String) {
    SideEffect {
        document.documentElement?.setAttribute("lang", languageCode)
    }
}