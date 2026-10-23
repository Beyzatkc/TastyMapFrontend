package org.beem.tastymap.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.beem.tastymap.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.data.model.auth.AuthStatus

class SplashViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _effect = MutableStateFlow<SplashEffect?>(null)
    val effect = _effect.asStateFlow()

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            // Eğer DeepLinkManager bekleyen bir rota tutuyorsa onu yakalıyoruz
            val deepLinkRoute = DeepLinkManager.pendingInitialRoute
            if (deepLinkRoute != null) {
                DeepLinkManager.pendingInitialRoute = null
                _effect.value = SplashEffect.NavigateToDeepLink(deepLinkRoute)
                return@launch
            }

            delay(600)

            when (authRepository.getAuthStatus()) {
                AuthStatus.AUTHENTICATED -> {
                    _effect.value = SplashEffect.NavigateToHome
                }
                AuthStatus.NEEDS_ONBOARDING -> {
                    _effect.value = SplashEffect.NavigateToOnBoard
                }
                AuthStatus.UNAUTHENTICATED -> {
                    _effect.value = SplashEffect.NavigateToLogin
                }
            }
        }
    }
}