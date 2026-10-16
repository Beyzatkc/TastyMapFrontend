package org.beem.tastymap.core.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.beem.tastymap.getPlatform

interface SettingsManager {
    val isDarkMode: StateFlow<Boolean?>
    fun setDarkMode(isEnabled: Boolean)

    val languageCode: StateFlow<String>
    fun setLanguageCode(code: String)
}
class SettingsManagerImpl(private val settings: Settings) : SettingsManager {
    companion object {
        private const val KEY_DARK_MODE = "is_dark_mode"
        private const val KEY_LANGUAGE = "selected_language_code"
        private const val DEFAULT_LANGUAGE = "tr"
    }

    private val _isDarkMode = MutableStateFlow<Boolean?>(settings[KEY_DARK_MODE])
    override val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private val _languageCode = MutableStateFlow(
        settings[KEY_LANGUAGE] ?: DEFAULT_LANGUAGE
    )
    override val languageCode: StateFlow<String> = _languageCode.asStateFlow()

    override fun setLanguageCode(code: String) {
        settings[KEY_LANGUAGE] = code
        _languageCode.value = code
    }

    override fun setDarkMode(isEnabled: Boolean) {
        settings[KEY_DARK_MODE] = isEnabled
        _isDarkMode.value = isEnabled
    }
}