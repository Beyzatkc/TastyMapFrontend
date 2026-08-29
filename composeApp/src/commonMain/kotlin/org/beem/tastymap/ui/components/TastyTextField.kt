package org.beem.tastymap.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.theme.LocalCustomColors

@Composable
fun TastyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val customColors = LocalCustomColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            isError = error != null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            leadingIcon = leadingIcon,
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    if (isPassword) {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Şifre Göster",
                                tint = customColors.textSecondary
                            )
                        }
                    }
                    if (error != null) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Hata",
                            tint = customColors.error
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = customColors.textPrimary,
                unfocusedTextColor = customColors.textPrimary,
                errorTextColor = customColors.textPrimary,

                focusedBorderColor = customColors.navy,
                unfocusedBorderColor = customColors.borderLight,
                errorBorderColor = customColors.error,

                focusedLabelColor = customColors.navy,
                unfocusedLabelColor = customColors.textSecondary,
                errorLabelColor = customColors.error,

                focusedLeadingIconColor = customColors.navy,
                unfocusedLeadingIconColor = customColors.textSecondary,
                errorLeadingIconColor = customColors.error,

                focusedTrailingIconColor = customColors.navy,
                unfocusedTrailingIconColor = customColors.textSecondary,
                errorTrailingIconColor = customColors.error,

                cursorColor = customColors.navy,
                errorCursorColor = customColors.error
            )
        )
        if (error != null) {
            Text(
                text = error,
                color = customColors.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}