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
}
class SettingsManagerImpl(private val settings: Settings) : SettingsManager {
    companion object {
        private const val KEY_DARK_MODE = "is_dark_mode"
    }

    private val _isDarkMode = MutableStateFlow<Boolean?>(settings[KEY_DARK_MODE])
    override val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    override fun setDarkMode(isEnabled: Boolean) {
        settings[KEY_DARK_MODE] = isEnabled
        _isDarkMode.value = isEnabled
    }
}