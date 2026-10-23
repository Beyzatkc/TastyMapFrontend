package org.beem.tastymap.ui.auth.forgotPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // <-- YENİ: Resmi NavController eklendi
import org.beem.tastymap.core.navigation.VerifyNavigator
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.auth.common.AuthLifecycleEvent
import org.beem.tastymap.ui.common.UnifiedLifecycleObserver
import org.beem.tastymap.ui.components.AuthFooter
import org.beem.tastymap.ui.components.BackPage
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel // <-- YENİ: koinScreenModel yerine bunu kullanıyoruz
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.*

@Composable
fun ForgotScreen(
    navController: NavController
) {
    val viewModel = koinViewModel<ForgotViewModel>()
    val state by viewModel.sendState.collectAsState()
    val colors = LocalCustomColors.current
    val navigationEffect by viewModel.navigationState.collectAsState(initial = null)
    val verifyNavigator = koinInject<VerifyNavigator>()

    UnifiedLifecycleObserver(
        onActive = {
            println("LIFECYCLE: Ekran aktif (Resume/Focus/Visible). Model tetikleniyor.")
            viewModel.handleLifecycleEvent(AuthLifecycleEvent.Resume)
        },
        onInactive = {
            println("LIFECYCLE: Ekran pasif (Stop/Blur/Hidden). Model temizleniyor.")
            viewModel.handleLifecycleEvent(AuthLifecycleEvent.Stop)
        }
    )

    LaunchedEffect(navigationEffect) {
        when (navigationEffect) {
            ForgotViewModel.PasswordNavEffect.OnSuccess -> {
                verifyNavigator.changePasswordOnSuccess(navController)
            }
            null -> Unit
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { message ->
            val text = when (message) {
                is ForgotViewModel.UiMessage.Dynamic -> message.message
                is ForgotViewModel.UiMessage.Resource -> getString(message.res)
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
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            BackPage(stringResource(Res.string.forgot_header), {
                viewModel.onBackClickForgot()
                navController.popBackStack()
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
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(Res.string.forgot_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(Res.string.forgot_subtitle),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    TastyTextField(
                        value = state.pasEmail,
                        onValueChange = {
                            viewModel.onEmailEvent(EmailEvent.EmailChanged(it))
                        },
                        label = stringResource(Res.string.forgot_field_email_or_username),
                        leadingIcon = {
                            Icon(Icons.Default.Email, null)
                        },
                        error = state.pasEmailError?.let { stringResource(it) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TastyButton(
                        text = stringResource(Res.string.forgot_btn_submit),
                        onClick = { viewModel.forgotPassword(state.pasEmail) },
                        isLoading = state.isLoading,
                        enabled = state.pasEmail.isNotBlank(),
                        backcolor = colors.navy,
                        textcolor = colors.surface,
                        strokecolor = Color.Transparent
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            viewModel.onBackClickForgot()
                            navController.popBackStack() // YENİ: navigator.pop() yerine resmi popBackStack()
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.forgot_btn_back_to_login),
                            color = colors.navy,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

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
                                text = stringResource(Res.string.forgot_info_registered_only),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )

                            Text(
                                text = stringResource(Res.string.forgot_info_validity),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )

                            Text(
                                text = stringResource(Res.string.forgot_info_spam_check),
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