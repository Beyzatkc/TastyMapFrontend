package org.beem.tastymap.ui.auth.splash

import cafe.adriel.voyager.core.model.ScreenModel
import org.beem.tastymap.data.repository.AuthRepository
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.beem.tastymap.core.navigation.DeepLinkManager

class SplashScreenModel(
    private val authRepository: AuthRepository
) : ScreenModel {

    private val _effect = MutableStateFlow<SplashEffect?>(null)
    val effect = _effect.asStateFlow()

    init {
        checkAuthentication()
    }
    private fun checkAuthentication() {
        screenModelScope.launch {
            val deepLinkScreen = DeepLinkManager.pendingInitialScreen
            if (deepLinkScreen != null) {
                DeepLinkManager.pendingInitialScreen = null
                _effect.value = SplashEffect.NavigateToDeepLink(deepLinkScreen)
                return@launch
            }
            delay(600)
            if (authRepository.isUserLoggedIn()) {
                _effect.value = SplashEffect.NavigateToHome
            } else {
                _effect.value = SplashEffect.NavigateToLogin
            }
        }
    }
}