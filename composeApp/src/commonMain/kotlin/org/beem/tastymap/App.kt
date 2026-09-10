package org.beem.tastymap

import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.db.SqlDriver
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.beem.tastymap.core.auth.AuthEventBus
import org.beem.tastymap.core.local.ChangeAppLanguage
import org.beem.tastymap.core.local.SettingsManager
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.database.TastyDatabase
import org.beem.tastymap.domain.auth.ClearSessionUseCase
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.components.AppToast
import org.beem.tastymap.ui.splash.SplashScreen
import org.beem.tastymap.ui.theme.TastyTheme
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.auth_logged_out
import tastymap.composeapp.generated.resources.auth_password_changed
import tastymap.composeapp.generated.resources.auth_session_expired

@Composable
@Preview
fun App() {
    val settingsManager: SettingsManager = koinInject()
    val isDarkModePref by settingsManager.isDarkMode.collectAsState()
    val languageCode by settingsManager.languageCode.collectAsState()

    val authEventBus: AuthEventBus = koinInject()
    val clearSessionUseCase: ClearSessionUseCase = koinInject()
    val sqlDriver: SqlDriver = koinInject()

    val useDarkTheme = isDarkModePref ?: isSystemInDarkTheme()

    // Platform bağımlı sistem Locale'ini güncelliyoruz
    ChangeAppLanguage(languageCode)

    // Veritabanı oluşturma işlemi
    LaunchedEffect(Unit) {
        try {
            TastyDatabase.Schema.create(sqlDriver).await()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            println("LOG_DB: Şema zaten mevcut veya pas geçildi: ${e.message}")
        }
    }

    TastyTheme(useDarkTheme = useDarkTheme) {
        Navigator(SplashScreen()) { navigator ->

            // DeepLink dinleyicisi
            LaunchedEffect(navigator) {
                DeepLinkManager.navigationEvents.collect { screen ->
                    if (navigator.lastItem !is SplashScreen) {
                        navigator.replaceAll(screen)
                    }
                }
            }

            // Oturum durum dinleyicisi
            LaunchedEffect(navigator) {
                authEventBus.events.collect { event ->
                    clearSessionUseCase()

                    val message = when (event) {
                        is AuthEventBus.AuthEvent.OnSessionExpired -> getString(Res.string.auth_session_expired)
                        is AuthEventBus.AuthEvent.OnPasswordChanged -> getString(Res.string.auth_password_changed)
                        is AuthEventBus.AuthEvent.OnLoggedOut -> getString(Res.string.auth_logged_out)
                    }

                    ToastManager.show(message)
                    navigator.replaceAll(LogRegScreen())
                }
            }

            // Dil değiştiğinde ekran geçiş alanını yeniden çizer
            key(languageCode) {
                SlideTransition(
                    navigator = navigator,
                    animationSpec = tween(400)
                )
            }
        }
        AppToast()
    }
}