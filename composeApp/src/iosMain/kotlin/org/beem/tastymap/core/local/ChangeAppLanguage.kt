package org.beem.tastymap.core.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.Foundation.NSUserDefaults

@Composable
actual fun ChangeAppLanguage(languageCode: String) {
    SideEffect {
        NSUserDefaults.standardUserDefaults.setObject(
            value = listOf(languageCode),
            forKey = "AppleLanguages"
        )
    }
}