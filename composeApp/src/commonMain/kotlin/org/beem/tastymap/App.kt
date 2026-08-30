package org.beem.tastymap

import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.db.SqlDriver
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import kotlinx.coroutines.launch
import org.beem.tastymap.core.auth.AuthEventBus
import org.beem.tastymap.core.local.SettingsManager
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.database.TastyDatabase
import org.beem.tastymap.domain.auth.ClearSessionUseCase
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.components.AppToast
import org.beem.tastymap.ui.splash.SplashScreen
import org.beem.tastymap.ui.theme.TastyTheme
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val settingsManager: SettingsManager = koinInject()
    val isDarkModePref by settingsManager.isDarkMode.collectAsState()

    val authEventBus: AuthEventBus = koinInject()
    val clearSessionUseCase: ClearSessionUseCase = koinInject()

    val sqlDriver: SqlDriver = koinInject()

    val useDarkTheme = isDarkModePref ?: isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        launch {
            try {
                TastyDatabase.Schema.create(sqlDriver).await()
            } catch (e: Exception) {
                // Tablo zaten var olduğunda veya Android/iOS tarafında hata fırlatıldığında akış bozulmaz
                println("LOG_DB: Şema zaten mevcut veya pas geçildi: ${e.message}")
            }
        }
    }

    TastyTheme(useDarkTheme = useDarkTheme) {
        Navigator(SplashScreen()) { navigator ->
            LaunchedEffect(Unit) {
                DeepLinkManager.navigationEvents.collect { screen ->
                    if (navigator.lastItem !is SplashScreen) {
                        navigator.replaceAll(screen)
                    }
                }
            }
            LaunchedEffect(Unit) {
                authEventBus.events.collect { event ->
                    clearSessionUseCase()
                    when (event) {
                        is AuthEventBus.AuthEvent.OnSessionExpired -> {
                            ToastManager.show("Oturum süreniz doldu, lütfen tekrar giriş yapın.")
                            navigator.replaceAll(LogRegScreen())
                        }
                        is AuthEventBus.AuthEvent.OnPasswordChanged -> {
                            ToastManager.show("Şifreniz değiştirildiği için oturumunuz kapatıldı.")
                            navigator.replaceAll(LogRegScreen())
                        }
                        is AuthEventBus.AuthEvent.OnLoggedOut -> {
                            ToastManager.show("Başarıyla çıkış yapıldı.")
                            navigator.replaceAll(LogRegScreen())
                        }
                    }
                }
            }
            SlideTransition(
                navigator = navigator,
                animationSpec = tween(400)
            )
        }
        AppToast()
    }
}