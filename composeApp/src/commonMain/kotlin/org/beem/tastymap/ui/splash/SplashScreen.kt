package org.beem.tastymap.ui.splash


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.beem.tastymap.ui.auth.LogRegRoute
import org.beem.tastymap.ui.bottomnav.MainRoute
import org.beem.tastymap.ui.profile.OnBoardingRoute
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.*

@Composable
fun SplashScreen(navController: NavController) {
    val screenModel = koinViewModel<SplashViewModel>()
    val effect by screenModel.effect.collectAsState()

    LaunchedEffect(effect) {
        effect?.let {
            delay(2000)
            when (it) {
                is SplashEffect.NavigateToDeepLink -> {
                    navController.navigate(it.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is SplashEffect.NavigateToLogin -> {
                    navController.navigate(LogRegRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is SplashEffect.NavigateToHome -> {
                    navController.navigate(MainRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is SplashEffect.NavigateToOnBoard -> {
                    navController.navigate(OnBoardingRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
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
                contentDescription = stringResource(Res.string.splash_app_logo_cd),
                modifier = Modifier.size(500.dp)
            )
        }
    }
}