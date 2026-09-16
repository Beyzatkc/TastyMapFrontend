package org.beem.tastymap.ui.profile.myprofile.settings.deleteaccount

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.beem.tastymap.core.auth.AuthEventBus
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.deleteaccount.DeleteReason
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import tastymap.composeapp.generated.resources.*

class DeleteAccountScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val deleteScreenModel = koinScreenModel<DeleteAccountScreenModel>()
        val uiState by deleteScreenModel.uiState.collectAsState()

        val customColors = LocalCustomColors.current
        val navigator = LocalNavigator.currentOrThrow
        var selectedReason by remember { mutableStateOf<DeleteReason?>(null) }
        var customReasonText by remember { mutableStateOf("") }
        var passwordText by remember { mutableStateOf("") }

        val isCustomReasonRequired = selectedReason == DeleteReason.OTHER

        val successMessage = stringResource(Res.string.delete_account_success_toast)

        LaunchedEffect(uiState.isSuccess) {
            if (uiState.isSuccess) {
                ToastManager.show(successMessage)
            }
        }


        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { message ->
                ToastManager.show(message)
                deleteScreenModel.clearMessages()
            }
        }

        val isCustomReasonValid = if (isCustomReasonRequired) {
            customReasonText.isNotBlank()
        } else {
            true
        }

        val isButtonEnabled = passwordText.isNotBlank() &&
                selectedReason != null &&
                isCustomReasonValid &&
                !uiState.isDeleteLoading

        Scaffold(
            containerColor = customColors.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.settings_delete_account),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = customColors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = customColors.textPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = customColors.background
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = customColors.error.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = customColors.error.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = customColors.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = stringResource(Res.string.delete_account_warning_title),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = customColors.error
                                )
                            )
                            Text(
                                text = stringResource(Res.string.delete_account_warning_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = customColors.textSecondary
                            )
                        }
                    }
                }

                // 2. Silme Sebebi Seçimi Section
                Column {
                    Text(
                        text = stringResource(Res.string.delete_account_reason_title),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = customColors.textPrimary
                        )
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            DeleteReason.entries.forEachIndexed { index, reason ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedReason = reason }
                                        .padding(horizontal = 3.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedReason == reason,
                                        onClick = { selectedReason = reason },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = customColors.gourmetOrange,
                                            unselectedColor = customColors.borderStrong
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = reason.toDisplayName(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = customColors.textPrimary
                                    )
                                }
                                if (index < DeleteReason.entries.size - 1) {
                                    HorizontalDivider(
                                        color = customColors.borderStrong.copy(alpha = 0.3f),
                                        thickness = 0.5.dp
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Özel Sebep Metin Alanı (TastyTextField)
                TastyTextField(
                    value = customReasonText,
                    onValueChange = { customReasonText = it },
                    label = if (isCustomReasonRequired) {
                        stringResource(Res.string.delete_account_custom_reason_title_must)
                    } else {
                        stringResource(Res.string.delete_account_custom_reason_title_ops)
                    },
                    singleLine = false,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                // 4. Şifre Doğrulama Alanı (TastyTextField)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TastyTextField(
                        value = passwordText,
                        onValueChange = { newValue ->
                            passwordText = newValue
                            if (uiState.passwordError != null) {
                                deleteScreenModel.clearPasswordError()
                            }
                        },
                        label = stringResource(Res.string.delete_account_password_title),
                        isPassword = true,
                        error = uiState.passwordError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = customColors.textSecondary
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = stringResource(Res.string.delete_account_password_helper),
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textTertiary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 5. Hesabı Sil Butonu (TastyButton)
                TastyButton(
                    text = stringResource(Res.string.delete_account_button),
                    onClick = {
                        deleteScreenModel.deleteAccount(
                            password = passwordText,
                            reasonType = selectedReason,
                            customReason = customReasonText.ifBlank { null }
                        )
                    },
                    isLoading = uiState.isDeleteLoading,
                    enabled = isButtonEnabled,
                    backcolor = customColors.error,
                    textcolor = customColors.surface,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable
    private fun DeleteReason.toDisplayName(): String = when (this) {
        DeleteReason.NOT_USING -> stringResource(Res.string.delete_reason_not_using)
        DeleteReason.APP_IS_SLOW -> stringResource(Res.string.delete_reason_app_is_slow)
        DeleteReason.TECHNICAL_ISSUES -> stringResource(Res.string.delete_reason_technical_issues)
        DeleteReason.PRIVACY_CONCERNS -> stringResource(Res.string.delete_reason_privacy_concerns)
        DeleteReason.POOR_RECOMMENDATIONS -> stringResource(Res.string.delete_reason_poor_recommendations)
        DeleteReason.MISSING_FEATURES -> stringResource(Res.string.delete_reason_missing_features)
        DeleteReason.DIFFICULT_TO_USE -> stringResource(Res.string.delete_reason_difficult_to_use)
        DeleteReason.FOUND_AN_ALTERNATIVE -> stringResource(Res.string.delete_reason_found_an_alternative)
        DeleteReason.OTHER -> stringResource(Res.string.delete_reason_other)
    }
}