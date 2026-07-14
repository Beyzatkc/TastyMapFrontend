package org.beem.tastymap.ui.auth.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.profile.health.HealthWizardScreen
import org.jetbrains.compose.resources.painterResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.app_logo


class SplashScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<SplashScreenModel>()
        val effect by screenModel.effect.collectAsState()

        LaunchedEffect(effect) {
            effect?.let {
                delay(2000)
                when (it) {
                    is SplashEffect.NavigateToDeepLink -> navigator.replaceAll(it.screen)
                    is SplashEffect.NavigateToLogin -> navigator.replaceAll(HealthWizardScreen())
                    is SplashEffect.NavigateToHome -> { /* navigator.replaceAll(HomeScreen()) */ }
                }
            }
        }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(500.dp)
                )
            }
        }
    }
}