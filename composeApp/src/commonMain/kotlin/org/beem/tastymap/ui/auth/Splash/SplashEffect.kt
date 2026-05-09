package org.beem.tastymap.ui.auth.Splash

import cafe.adriel.voyager.core.screen.Screen
import org.beem.tastymap.ui.auth.AuthEffect

sealed interface SplashEffect {
    object NavigateToHome : SplashEffect

    object NavigateToLogin : SplashEffect
    data class NavigateToDeepLink(val screen : Screen) : SplashEffect
}