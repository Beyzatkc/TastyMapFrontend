package org.beem.tastymap.ui.auth

import TastyButton
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

class VerifyScreen(val token: String) : Screen{

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val koinInstance = koinInject<AuthScreenModel>()
        val screenModel = rememberScreenModel { koinInstance }
        val state by screenModel.registerState.collectAsState()

        LaunchedEffect(token) {
            screenModel.verifyEmail(token)
        }

        LaunchedEffect(Unit) {
            screenModel.navigationEvent.collect { isSuccess ->
                if (isSuccess) {
                    delay(1000)
                    //navigator.replaceAll(HomeScreen())
                } else {

                    //navigator.replaceAll(LogRegScreen())
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = state.verificationError != null,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.92f))
                            .togetherWith(fadeOut(animationSpec = tween(500)))
                    },
                    label = "VerifyStateAnim"
                ) { isError ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!isError) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Hesabınız Doğrulanıyor...",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Text(
                                text = "Lütfen bekleyiniz, giriş sayfaya yönlendiriliyorsunuz.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {

                            Icon(
                                imageVector = Icons.Default.ErrorOutline, // Import gerekli
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Bir Sorun Oluştu",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.verificationError ?: "Bilinmeyen bir hata",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            TastyButton(
                                text = "Geri Dön",
                                onClick = { navigator.replaceAll(LogRegScreen()) },
                                backcolor = MaterialTheme.colorScheme.error,
                                textcolor = Color.White,
                                strokecolor = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}