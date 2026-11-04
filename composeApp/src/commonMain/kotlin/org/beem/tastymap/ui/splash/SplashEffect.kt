package org.beem.tastymap.ui.splash


sealed interface SplashEffect {
    object NavigateToHome : SplashEffect
    object NavigateToOnBoard : SplashEffect

    object NavigateToLogin : SplashEffect
    data class NavigateToDeepLink(val route: Any) : SplashEffect
}