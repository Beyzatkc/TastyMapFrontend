package org.beem.tastymap.ui.components
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.alert_cancel

@Composable
fun TastyInputDialog(
    title: String,
    initialText: String,
    confirmText: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    val customColors = LocalCustomColors.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = customColors.surface,
        titleContentColor = customColors.textPrimary,
        textContentColor = customColors.textSecondary,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = customColors.textPrimary
            )
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = customColors.textPrimary,
                    unfocusedTextColor = customColors.textPrimary,
                    focusedBorderColor = customColors.gourmetOrange,
                    unfocusedBorderColor = customColors.surfaceVariant,
                    cursorColor = customColors.gourmetOrange
                )
            )
        },
        confirmButton = {
            val isEnabled = text.isNotBlank() && text != initialText

            TextButton(
                onClick = {
                    onConfirm(text)
                },
                enabled = isEnabled
            ) {
                Text(
                    text = confirmText,
                    color = if (isEnabled) customColors.gourmetOrange else customColors.textSecondary.copy(alpha = 0.5f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    text = stringResource(Res.string.alert_cancel),
                    color = customColors.textSecondary
                )
            }
        }
    )
}