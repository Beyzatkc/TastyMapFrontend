package org.beem.tastymap.ui.profile.myprofile

import TastyButton
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordBottomSheet(
    isActionLoading: Boolean = false,
    onDismissRequest: () -> Unit = {},
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
        containerColor = MaterialTheme.colorScheme.surface
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
                text = "Şifre Değiştir",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.25f)
            )

            Spacer(modifier = Modifier.height(20.dp))


            // Mevcut Şifre
            TastyTextField(
                value = oldPassword,
                isPassword = true,
                onValueChange = { oldPassword = it },
                label = "Mevcut Şifre",
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Yeni Şifre
            TastyTextField(
                value = newPassword,
                isPassword = true,
                onValueChange = { newPassword = it },
                label = "Yeni Şifre",
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                }
            )

            // Canlı Şifre Güç Göstergesi
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
                onValueChange = { againNew = it },
                label = "Yeni Şifre (Tekrar)",
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TastyButton(
                text = "Şifreyi Güncelle",
                onClick = {
                    onSubmitClick(oldPassword, newPassword, againNew)
                },
                modifier = Modifier.fillMaxWidth(),
                isLoading = isActionLoading,
                enabled = oldPassword.isNotBlank() && newPassword.isNotBlank() && againNew.isNotBlank(),
                backcolor = customColors.navy,
                textcolor = Color.White,
                strokecolor = customColors.navy
            )
        }
    }
}