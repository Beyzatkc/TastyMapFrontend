package org.beem.tastymap.ui.profile.myprofile

import org.beem.tastymap.data.model.profile.ActiveDeviceDTO
import org.beem.tastymap.data.model.profile.ActiveDevicesResponse
import org.beem.tastymap.domain.model.UserProfile

data class MyProfileUiState(
    // Genel Profil Yükleme Durumu
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val errorMessage: String? = null,

    // Buton/Aksiyon Loading Durumları
    val isActionLoading: Boolean = false,
    val successMessage: String? = null,

    val activeDeviceCount: Long = 0,
    val activeDevices: List<ActiveDeviceDTO> = emptyList(), // Doğru tip: ActiveDeviceDTO
    val isDevicesLoading: Boolean = false

)