package org.beem.tastymap.ui.profile.myprofile.settings.changepassword

import TastyButton
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.auth.common.PasswordStrength
import org.beem.tastymap.ui.components.PasswordStrengthIndicator
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.change_password_current
import tastymap.composeapp.generated.resources.change_password_new
import tastymap.composeapp.generated.resources.change_password_new_again
import tastymap.composeapp.generated.resources.change_password_submit_btn
import tastymap.composeapp.generated.resources.change_password_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordBottomSheet(
    isActionLoading: Boolean = false,
    errorMessage: String? = null,
    oldPasswordError: String? = null,
    newPasswordError: String? = null,
    againNewPasswordError: String? = null,
    onDismissRequest: () -> Unit = {},
    onClearError: () -> Unit = {},
    onSubmitClick: (oldPassword: String, newPassword: String, againNew: String) -> Unit = { _, _, _ -> }
) {
    val customColors = LocalCustomColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var againNew by remember { mutableStateOf("") }

    val passwordStrength = remember(newPassword) {
        PasswordStrength(
            hasMinLength = newPassword.length >= 8,
            hasUppercase = newPassword.any { it.isUpperCase() },
            hasDigit = newPassword.any { it.isDigit() },
            hasSpecialChar = newPassword.contains(Regex("[@#\$!%^&*(),.?\":{}|<>]"))
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = customColors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(Res.string.change_password_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = customColors.textPrimary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Mevcut Şifre
            TastyTextField(
                value = oldPassword,
                isPassword = true,
                onValueChange = {
                    oldPassword = it
                    onClearError()
                },
                label = stringResource(Res.string.change_password_current),
                error = oldPasswordError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = customColors.textSecondary
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Yeni Şifre
            TastyTextField(
                value = newPassword,
                isPassword = true,
                onValueChange = {
                    newPassword = it
                    onClearError()
                },
                label = stringResource(Res.string.change_password_new),
                error = newPasswordError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = customColors.textSecondary
                    )
                }
            )

            AnimatedVisibility(
                visible = newPassword.isNotEmpty(),
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(4.dp))
                    PasswordStrengthIndicator(passwordStrength, customColors)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Yeni Şifre Tekrar
            TastyTextField(
                value = againNew,
                isPassword = true,
                onValueChange = {
                    againNew = it
                    onClearError()
                },
                label = stringResource(Res.string.change_password_new_again),
                error = againNewPasswordError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = customColors.textSecondary
                    )
                }
            )

            // GENEL API HATA MESAJI ALANI
            AnimatedVisibility(
                visible = !errorMessage.isNullOrBlank(),
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = customColors.error.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = customColors.error,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TastyButton(
                text = stringResource(Res.string.change_password_submit_btn),
                onClick = {
                    onSubmitClick(oldPassword, newPassword, againNew)
                },
                modifier = Modifier.fillMaxWidth(),
                isLoading = isActionLoading,
                enabled = oldPassword.isNotBlank() && newPassword.isNotBlank() && againNew.isNotBlank(),
                backcolor = customColors.gourmetOrange,
                textcolor = customColors.surface,
                strokecolor = Color.Transparent
            )
        }
    }
}