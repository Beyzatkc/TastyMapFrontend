package org.beem.tastymap.ui.profile.myprofile.settings.activedevices

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.util.formatToRelativeDateTime
import org.beem.tastymap.core.util.parseDeviceName
import org.beem.tastymap.data.repository.profile.MyProfileRepository

class ActiveDevicesScreenModel(
    private val repo: MyProfileRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(ActiveDevicesUiState())
    val uiState = _uiState.asStateFlow()

    private fun detectDeviceType(userAgent: String): DeviceType {
        val combined = "$userAgent".lowercase()
        return when {
            combined.contains("android") -> DeviceType.ANDROID
            combined.contains("iphone") || combined.contains("ipad") || combined.contains("ios") -> DeviceType.IOS
            combined.contains("mozilla") || combined.contains("chrome") || combined.contains("safari") || combined.contains("web") -> DeviceType.WEB
            else -> DeviceType.UNKNOWN
        }
    }
    fun getActiveDevices() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repo.getActiveDevices()) {
                is ResultWrapper.Success -> {
                    val formattedDevices = result.data.devices.map { dto ->
                        ActiveDeviceUiItem(
                            deviceId = dto.deviceId,
                            deviceName = dto.userAgent.parseDeviceName(),
                            formattedLastSeen = "Giriş yapıldı: ${formatToRelativeDateTime(dto.lastUsedAt)}",
                            location = dto.city,
                            deviceType = detectDeviceType(dto.userAgent)
                        )
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            activeDeviceCount = result.data.activeDeviceCount,
                            devices = formattedDevices
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}