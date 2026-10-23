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
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.edit_post
import tastymap.composeapp.generated.resources.pin_post
import tastymap.composeapp.generated.resources.profile_action_reject
import tastymap.composeapp.generated.resources.profile_action_report
import tastymap.composeapp.generated.resources.unpin_post

@Composable
fun CommentActionDropdownMenu(
    expanded: Boolean,
    isOwnComment: Boolean,
    isOwnPost: Boolean,
    isPinned: Boolean = false,
    showPinOption: Boolean = true,
    onDismissRequest: () -> Unit,
    onPinClick: () -> Unit = {},
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    val canPin = isOwnPost && showPinOption
    val canUpdate = isOwnComment
    val canDelete = isOwnComment || isOwnPost
    val canReport = !isOwnComment && !isOwnPost

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        containerColor = customColors.surface
    ) {
        if (canPin) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = if (isPinned) stringResource(Res.string.unpin_post) else stringResource(Res.string.pin_post),
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

        if (canUpdate) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.edit_post), color = customColors.textPrimary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = customColors.textPrimary
                    )
                },
                onClick = {
                    onDismissRequest()
                    onUpdateClick()
                }
            )
        }

        if (canDelete) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.profile_action_reject), color = customColors.red) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = customColors.red
                    )
                },
                onClick = {
                    onDismissRequest()
                    onDeleteClick()
                }
            )
        }

        if (canReport) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.profile_action_report), color = customColors.red) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = customColors.red
                    )
                },
                onClick = {
                    onDismissRequest()
                    onReportClick()
                }
            )
        }
    }
}