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
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.verify_error_default
import tastymap.composeapp.generated.resources.verify_error_title
import tastymap.composeapp.generated.resources.verify_header
import tastymap.composeapp.generated.resources.verify_loading_subtitle
import tastymap.composeapp.generated.resources.verify_loading_title

class VerifyScreen(val token: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<EmailScreenModel>()
        val state by screenModel.verificationState.collectAsState()
        val verifyNavigator = koinInject<VerifyNavigator>()
        val colors = LocalCustomColors.current

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
                val text = when (message) {
                    is EmailScreenModel.UiMessage.Dynamic -> message.message
                    is EmailScreenModel.UiMessage.Resource -> getString(message.res)
                }
                ToastManager.show(text)
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colors.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                BackPage(
                    header = stringResource(Res.string.verify_header),
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
                                        color = colors.navy,
                                        strokeWidth = 4.dp
                                    )

                                    Spacer(Modifier.height(32.dp))

                                    Text(
                                        text = stringResource(Res.string.verify_loading_title),
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = colors.textPrimary,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        text = stringResource(Res.string.verify_loading_subtitle),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.textSecondary,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 22.sp
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .background(
                                                colors.error.copy(alpha = 0.12f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ErrorOutline,
                                            contentDescription = null,
                                            tint = colors.error,
                                            modifier = Modifier.size(56.dp)
                                        )
                                    }

                                    Spacer(Modifier.height(24.dp))

                                    Text(
                                        text = stringResource(Res.string.verify_error_title),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = colors.error,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        text = state.verificationError ?: stringResource(Res.string.verify_error_default),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = colors.textSecondary,
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