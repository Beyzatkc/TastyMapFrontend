package org.beem.tastymap.ui.post.postcomment.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.beem.tastymap.ui.theme.LocalCustomColors


@Composable
fun CommentActionDropdownMenu(
    expanded: Boolean,
    isOwnComment: Boolean,
    isPinned: Boolean = false,
    showPinOption: Boolean = true,
    onDismissRequest: () -> Unit,
    onPinClick: () -> Unit = {},
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        containerColor = customColors.background
    ) {
        if (isOwnComment) {
            if (showPinOption) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (isPinned) "Sabitlemeyi Kaldır" else "Pinle",
                            color = customColors.textPrimary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null,
                            tint = customColors.textPrimary
                        )
                    },
                    onClick = {
                        onDismissRequest()
                        onPinClick()
                    }
                )
            }

            DropdownMenuItem(
                text = { Text("Güncelle", color = customColors.textPrimary) },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = customColors.textPrimary) },
                onClick = {
                    onDismissRequest()
                    onUpdateClick()
                }
            )
            DropdownMenuItem(
                text = { Text("Sil", color = customColors.red) },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = customColors.red) },
                onClick = {
                    onDismissRequest()
                    onDeleteClick()
                }
            )
        } else {
            DropdownMenuItem(
                text = { Text("Şikayet Et", color = customColors.red) },
                leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = customColors.red) },
                onClick = {
                    onDismissRequest()
                    onReportClick()
                }
            )
        }
    }
}