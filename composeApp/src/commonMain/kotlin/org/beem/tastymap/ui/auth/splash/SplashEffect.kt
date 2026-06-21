package org.beem.tastymap.ui.auth.splash

import cafe.adriel.voyager.core.screen.Screen

sealed interface SplashEffect {
    object NavigateToHome : SplashEffect

    object NavigateToLogin : SplashEffect
    data class NavigateToDeepLink(val screen : Screen) : SplashEffect
}