package org.beem.tastymap.ui.auth.verification

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import org.beem.tastymap.core.constants.AuthConstants
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.core.navigation.PlatformMessenger
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.animations.TastyAnimations
import org.koin.compose.koinInject

class VerifyScreen(val token: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val koinInstance = koinInject<EmailScreenModel>()
        val messenger = koinInject<PlatformMessenger>()
        val screenModel = rememberScreenModel { koinInstance }
        val state by screenModel.verificationState.collectAsState()


        LaunchedEffect(state.isEmailVerified) {
            if (state.isEmailVerified) {
                messenger.post(AuthConstants.MSG_VERIFICATION_FINISHED)
                delay(2000)
                navigator.replaceAll(VerificationSuccessScreen())
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
            modifier = Modifier.Companion.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            Box(
                modifier = Modifier.Companion.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Companion.Center
            ) {
                AnimatedContent(
                    targetState = state.verificationError != null,
                    transitionSpec = {
                        TastyAnimations.scaleFade()
                    },
                    label = "VerifyStateAnim"
                ) { isError ->

                    Column(
                        modifier = Modifier.Companion
                            .widthIn(max = 440.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Companion.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (!isError) {
                            CircularProgressIndicator(
                                modifier = Modifier.Companion.size(54.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp
                            )

                            Spacer(modifier = Modifier.Companion.height(32.dp))

                            Text(
                                text = "Hesabınız Doğrulanıyor",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Companion.Center,
                                fontWeight = FontWeight.Companion.Bold
                            )

                            Spacer(modifier = Modifier.Companion.height(12.dp))

                            Text(
                                text = "Lütfen bekleyiniz.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Companion.Center,
                                lineHeight = 22.sp
                            )
                        } else {
                            Column(
                                modifier = Modifier.Companion
                                    .widthIn(max = 440.dp)
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.Companion.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier.Companion
                                        .size(100.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.errorContainer.copy(
                                                alpha = 0.4f
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Companion.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.Companion.size(56.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.Companion.height(24.dp))

                                Text(
                                    text = "Bir Sorun Oluştu",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Companion.ExtraBold,
                                    textAlign = TextAlign.Companion.Center
                                )

                                Spacer(modifier = Modifier.Companion.height(12.dp))

                                Text(
                                    text = state.verificationError
                                        ?: "Beklenmedik bir hata ile karşılaşıldı. Lütfen internet bağlantınızı kontrol edip tekrar deneyiniz.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Companion.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 24.sp
                                )

                            }
                        }
                    }
                }
            }
        }
    }
}