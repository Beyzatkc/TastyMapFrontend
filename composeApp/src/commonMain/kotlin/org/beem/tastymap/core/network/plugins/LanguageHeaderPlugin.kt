package org.beem.tastymap.core.network.plugins

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import org.beem.tastymap.core.local.SettingsManager

class LanguageHeaderConfig {
    lateinit var settingsManager: SettingsManager
}

val LanguageHeaderPlugin = createClientPlugin(
    name = "LanguageHeaderPlugin",
    createConfiguration = ::LanguageHeaderConfig
) {
    val settingsManager = pluginConfig.settingsManager

    onRequest { request, _ ->
        val currentLanguage = settingsManager.languageCode.value.ifEmpty { "tr" }

        request.headers.remove("Accept-Language")
        request.header("Accept-Language", currentLanguage)
    }
}