package org.beem.tastymap.ui.auth.verification.email

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.core.navigation.VerifyNavigator
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.animations.TastyAnimations
import org.beem.tastymap.ui.components.AuthFooter
import org.beem.tastymap.ui.components.BackPage
import org.koin.compose.koinInject

class VerifyScreen(val token: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<EmailScreenModel>()
        val state by screenModel.verificationState.collectAsState()
        val verifyNavigator = koinInject<VerifyNavigator>()

        LaunchedEffect(state.isEmailVerified) {
            if (state.isEmailVerified) {
                delay(2000)
                verifyNavigator.verifyEmailOnSuccess(navigator)
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                DeepLinkManager.clear()
            }
        }

        LaunchedEffect(Unit) {
            screenModel.verifyEmail(token)
        }

        LaunchedEffect(screenModel.uiMessage) {
            screenModel.uiMessage.collect { message ->
                ToastManager.show(message)
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
            ) {

                BackPage(
                    header = "Hesap Doğrulama",
                    onBackClick = { navigator.pop() }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        modifier = Modifier
                            .widthIn(max = 480.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        AnimatedContent(
                            targetState = state.verificationError != null,
                            transitionSpec = { TastyAnimations.scaleFade() },
                            label = "VerifyStateAnim"
                        ) { isError ->

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                if (!isError) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(54.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 4.dp
                                    )

                                    Spacer(Modifier.height(32.dp))

                                    Text(
                                        text = "Hesabınız Doğrulanıyor",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        text = "Lütfen bekleyiniz.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 22.sp
                                    )

                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .background(
                                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ErrorOutline,
                                            null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(56.dp)
                                        )
                                    }

                                    Spacer(Modifier.height(24.dp))

                                    Text(
                                        text = "Bir Sorun Oluştu",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        text = state.verificationError
                                            ?: "Beklenmedik bir hata oluştu. Lütfen tekrar deneyiniz.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                        }
                    }
                }
                AuthFooter(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}