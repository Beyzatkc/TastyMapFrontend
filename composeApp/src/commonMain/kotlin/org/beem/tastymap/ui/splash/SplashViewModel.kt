package org.beem.tastymap.ui.splash

import ProfileGraphRoute
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import org.beem.tastymap.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.data.model.auth.AuthStatus
import org.beem.tastymap.ui.auth.ResetRoute
import org.beem.tastymap.ui.auth.VerifyRoute
import org.beem.tastymap.ui.profile.ProfileRoute

class SplashViewModel(
    private val authRepository: AuthRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _effect = MutableStateFlow<SplashEffect?>(null)
    val effect = _effect.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            checkAuthentication()
        }
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            // Eğer DeepLinkManager bekleyen bir rota tutuyorsa onu yakalıyoruz
            val deepLinkRoute = DeepLinkManager.pendingInitialRoute
            if (deepLinkRoute is VerifyRoute || deepLinkRoute is ResetRoute) {
                DeepLinkManager.pendingInitialRoute = null
                _effect.value = SplashEffect.NavigateToDeepLink(deepLinkRoute)
                return@launch
            }

            delay(600)

            when (authRepository.getAuthStatus()) {
                AuthStatus.AUTHENTICATED -> {
                    var refreshRoute = DeepLinkManager.pendingRefreshRoute

                    // ÖNEMLİ DÜZELTME: Kullanıcı verisi yüklendikten sonra kontrol et
                    if (refreshRoute is ProfileRoute) {
                        val myUsername = userManager.userSession.value?.username
                        if (!myUsername.isNullOrBlank() && refreshRoute.username.equals(myUsername, ignoreCase = true)) {
                            // URL'deki isim kendi adımızsa, kendi profil ana rotamıza çevir
                            refreshRoute = ProfileGraphRoute
                        }
                    }

                    if (refreshRoute != null) {
                        DeepLinkManager.pendingRefreshRoute = null
                        _effect.value = SplashEffect.NavigateToDeepLink(refreshRoute)
                    } else {
                        _effect.value = SplashEffect.NavigateToHome
                    }
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