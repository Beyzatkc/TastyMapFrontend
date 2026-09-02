package org.beem.tastymap.ui.auth.verification.email

import TastyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.core.navigation.VerifyNavigator
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.auth.common.AuthLifecycleEvent
import org.beem.tastymap.ui.auth.forgotPassword.ResetScreenModel
import org.beem.tastymap.ui.auth.logReg.LogRegScreen

import org.beem.tastymap.ui.common.UnifiedLifecycleObserver
import org.beem.tastymap.ui.components.AuthFooter
import org.beem.tastymap.ui.components.BackPage
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.email_verify_back_to_login
import tastymap.composeapp.generated.resources.email_verify_desc
import tastymap.composeapp.generated.resources.email_verify_header
import tastymap.composeapp.generated.resources.email_verify_resend_prompt
import tastymap.composeapp.generated.resources.email_verify_resend_timer
import tastymap.composeapp.generated.resources.email_verify_security_desc
import tastymap.composeapp.generated.resources.email_verify_security_title
import tastymap.composeapp.generated.resources.email_verify_spam_note
import tastymap.composeapp.generated.resources.email_verify_title

class EmailVerificationScreen(val email: String, val deviceId: String, val userId: Long) : Screen {
    @Composable
    override fun Content() {
        val colors = LocalCustomColors.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<EmailScreenModel>()
        val navigationEffect by screenModel.navigationState.collectAsState(initial = null)
        val verifyNavigator = koinInject<VerifyNavigator>()

        LaunchedEffect(navigationEffect) {
            when (navigationEffect) {
                EmailScreenModel.EmailNavEffect.OnSuccess -> {
                    verifyNavigator.verifyEmailNavigationTwo(navigator)
                }
                null -> Unit
            }
        }

        LaunchedEffect(Unit) {
            screenModel.uiMessage.collect { message ->
                val text = when (message) {
                    is EmailScreenModel.UiMessage.Dynamic -> message.message
                    is EmailScreenModel.UiMessage.Resource -> getString(message.res)
                }
                ToastManager.show(text)
            }
        }

        LaunchedEffect(userId, deviceId) {
            screenModel.setVerificationContext(
                userId,
                deviceId
            )
        }

        UnifiedLifecycleObserver(
            onActive = {
                println("LIFECYCLE: Ekran aktif (Resume/Focus/Visible). Model tetikleniyor.")
                screenModel.handleLifecycleEvent(AuthLifecycleEvent.Resume)
            },
            onInactive = {
                println("LIFECYCLE: Ekran pasif (Stop/Blur/Hidden). Model temizleniyor.")
                screenModel.handleLifecycleEvent(AuthLifecycleEvent.Stop)
            }
        )

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
                BackPage(stringResource(Res.string.email_verify_header), {
                    navigator.pop()
                })
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 480.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(
                                    color = colors.surfaceVariant,
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = colors.textPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(Res.string.email_verify_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(Res.string.email_verify_desc, email),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        ResendSection(
                            navyIcons = colors.navy,
                            screenModel = screenModel,
                            onResendClick = { screenModel.resendMail(deviceId, email) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TastyButton(
                            text = stringResource(Res.string.email_verify_back_to_login),
                            onClick = {
                                if (navigator.canPop) {
                                    navigator.pop()
                                } else {
                                    navigator.replaceAll(LogRegScreen())
                                }
                            },
                            backcolor = colors.navy,
                            textcolor = colors.surface,
                            strokecolor = Color.Transparent
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colors.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(stringResource(Res.string.email_verify_security_title))
                                        }
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                                            append(stringResource(Res.string.email_verify_security_desc))
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textSecondary
                                )

                                Text(
                                    text = stringResource(Res.string.email_verify_spam_note),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
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

@Composable
fun ResendSection(onResendClick: () -> Unit, navyIcons: Color, screenModel: EmailScreenModel) {
    val timeLeft by screenModel.timeLeft.collectAsState()
    val isButtonEnabled = timeLeft == 0
    val customColors = LocalCustomColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            enabled = isButtonEnabled,
            onClick = {
                if (isButtonEnabled) {
                    screenModel.startTimer()
                    onResendClick()
                }
            }
        ) {
            Text(
                text = if (isButtonEnabled) {
                    stringResource(Res.string.email_verify_resend_prompt)
                } else {
                    stringResource(Res.string.email_verify_resend_timer, timeLeft)
                },
                color = if (isButtonEnabled) navyIcons else customColors.textSecondary,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}