package org.beem.tastymap.core.local

import android.annotation.SuppressLint
import android.os.Build
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@SuppressLint("LocalContextConfigurationRead")
@Composable
actual fun ChangeAppLanguage(languageCode: String) {
    val context = LocalContext.current
    SideEffect {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}