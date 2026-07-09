package org.beem.tastymap.ui.auth.forgotPassword

import TastyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.components.AuthFooter
import org.beem.tastymap.ui.components.BackPage
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.koin.compose.koinInject

class ResetScreen(): Screen {
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val koinInstance = koinInject<ForgotScreenModel>()
        val screenModel = rememberScreenModel { koinInstance }
        val state by screenModel.passwordState.collectAsState()
        val colors = LocalCustomColors.current


        LaunchedEffect(screenModel.uiMessage) {
            screenModel.uiMessage.collect { message ->
                ToastManager.show(message)
            }
        }

        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                BackPage("Şifre Sıfırlama", {
                   // screenModel.onBackClick()
                   navigator.pop()
                })
                Box(
                    modifier = Modifier.fillMaxWidth(),
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
                                    color = colors.navy.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = colors.navy
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Şifrenizi mi Unuttunuz?",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.navy,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        TastyTextField(
                            value = state.regPassword,
                            isPassword = true,
                            onValueChange = {
                                screenModel.onEmailEvent(EmailEvent.EmailChanged(it))
                            },
                            label = "Yeni Şifre",
                            leadingIcon = {
                                Icon(Icons.Default.Lock, null)
                            },
                            error = state.regPasswordError
                        )
                        TastyTextField(
                            value = state.confirmPassword,
                            isPassword = true,
                            onValueChange = {
                                screenModel.onEmailEvent(EmailEvent.EmailChanged(it))
                            },
                            label = "Yeni Şifre tekrar",
                            leadingIcon = {
                                Icon(Icons.Default.Lock, null)
                            },
                            error = state.confirmPasswordError
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        TastyButton(
                            text = "Şifreyi değiştir",
                            onClick = { screenModel.resetPassword(tokenı vercez) },
                            isLoading = state.isLoading,
                            enabled = state.regPassword.isNotBlank() && state.confirmPassword.isNotBlank(),
                            backcolor = colors.navy,
                            textcolor = Color.White,
                            strokecolor = colors.navy
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = {
                                //screenModel.onBackClick()
                                navigator.pop()
                            }
                        ) {
                            Text(
                                text = "Giriş ekranına geri dön",
                                color = colors.navy,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colors.navy.copy(alpha = 0.05f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Şifrenizi sıfırladıktan sonra hesabınıza giriş yapabilirsiniz.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                            }
                        }

                        AuthFooter()
                    }
                }
            }
        }
    }

}