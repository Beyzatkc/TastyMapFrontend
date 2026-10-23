package org.beem.tastymap.ui.auth.verification.loginPending

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.auth.LogRegRoute
import org.beem.tastymap.ui.auth.LoginPendingRoute
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.common.AuthLifecycleEvent
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.components.AuthFooter
import org.beem.tastymap.ui.profile.myprofile.settings.UiMessage
import org.beem.tastymap.ui.splash.SplashRoute
import org.beem.tastymap.ui.splash.SplashScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.pending_email_sent_desc
import tastymap.composeapp.generated.resources.pending_email_sent_title
import tastymap.composeapp.generated.resources.pending_resend_email
import tastymap.composeapp.generated.resources.pending_resend_timer
import tastymap.composeapp.generated.resources.pending_security_desc
import tastymap.composeapp.generated.resources.pending_security_title
import tastymap.composeapp.generated.resources.pending_waiting
import tastymap.composeapp.generated.resources.pending_warning_note

@Composable
fun PendingScreen(
    deviceId: String,
    navController: NavController
) {
    val viewModel = koinViewModel<PendingViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val colors = LocalCustomColors.current

    LaunchedEffect(Unit) {
        viewModel.pendingLogin.collect { pendingLogin ->
            when (pendingLogin) {
                is AuthEffect.NavigateToHome -> {
                    navController.navigate(SplashRoute) { popUpTo(0) { inclusive = true } }
                }
                is AuthEffect.NavigateToLogin -> {
                    navController.navigate(LogRegRoute) { popUpTo(0) { inclusive = true } }
                }
                is AuthEffect.NavigateToPending -> {
                    navController.navigate(LoginPendingRoute(pendingLogin.deviceId)) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                else -> Unit
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { message ->
            val text = when (message) {
                is UiMessage.Dynamic -> message.message
                is UiMessage.Resource -> getString(message.res)
            }
            ToastManager.show(text)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.onLifecycleEvent(AuthLifecycleEvent.Resume, deviceId)
                }
                Lifecycle.Event.ON_STOP -> {
                    viewModel.onLifecycleEvent(AuthLifecycleEvent.Stop, deviceId)
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = colors.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Security,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = colors.textPrimary
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = stringResource(Res.string.pending_security_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.pending_security_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(20.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = colors.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = colors.textPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(Res.string.pending_email_sent_title),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = colors.textPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = stringResource(Res.string.pending_email_sent_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.5.dp,
                            color = colors.navy
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.pending_waiting),
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textSecondary,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = colors.error.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = stringResource(Res.string.pending_warning_note),
                                modifier = Modifier.padding(12.dp),
                                color = colors.error,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        ResendSection(
                            navyIcons = colors.navy,
                            viewModel = viewModel,
                            onResendClick = { viewModel.resendEmail(deviceId) }
                        )
                    }
                }
                AuthFooter(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }


@Composable
fun ResendSection(
    onResendClick: () -> Unit,
    navyIcons: Color,
    viewModel: PendingViewModel
) {
    val timeLeft by viewModel.timeLeft.collectAsState()
    val state by viewModel.sendState.collectAsState()
    val customColors = LocalCustomColors.current

    val isButtonEnabled = timeLeft == 0 && !state.isLoading

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(
            enabled = isButtonEnabled,
            onClick = {
                if (isButtonEnabled) {
                    viewModel.startTimer()
                    onResendClick()
                }
            }
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = navyIcons)
            } else {
                Text(
                    text = if (timeLeft == 0) {
                        stringResource(Res.string.pending_resend_email)
                    } else {
                        stringResource(Res.string.pending_resend_timer, timeLeft)
                    },
                    color = if (isButtonEnabled) navyIcons else customColors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}