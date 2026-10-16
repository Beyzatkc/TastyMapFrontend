package org.beem.tastymap.ui.profile.myprofile.settings.activedevices

import org.beem.tastymap.data.model.profile.ActiveDeviceDTO


enum class DeviceType {
    ANDROID,
    IOS,
    WEB,
    UNKNOWN
}

data class ActiveDeviceUiItem(
    val deviceId: String,
    val deviceName: String,
    val formattedLastSeen: String,
    val location: String?,
    val deviceType: DeviceType
)

data class ActiveDevicesUiState(
    val isLoading: Boolean = false,
    val activeDeviceCount: Long = 0L,
    val devices: List<ActiveDeviceUiItem> = emptyList(),
    val errorMessage: String? = null
)