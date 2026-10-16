package org.beem.tastymap.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.alert_cancel

data class DialogConfig(
    val title: String,
    val message: String,
    val confirmText: String,
    val isDestructive: Boolean = true,
    val onConfirm: () -> Unit
)

@Composable
fun TastyConfirmDialog(
    config: DialogConfig,
    onDismiss: () -> Unit
) {
    val customColors = LocalCustomColors.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = customColors.surface,
        titleContentColor = customColors.textPrimary,
        textContentColor = customColors.textSecondary,
        title = {
            Text(
                text = config.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = customColors.textPrimary
            )
        },
        text = {
            Text(
                text = config.message,
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textSecondary
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    config.onConfirm()
                    onDismiss()
                }
            ) {
                Text(
                    text = config.confirmText,
                    color = customColors.red,
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