package org.beem.tastymap.ui.profile.otherprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.beem.tastymap.LocalIsWideScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.profile_action_block
import tastymap.composeapp.generated.resources.profile_action_remove_follower
import tastymap.composeapp.generated.resources.profile_action_report
import tastymap.composeapp.generated.resources.profile_action_unblock


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileOptionsBottomSheet(
    isBlockedByMe: Boolean,
    isFollower: Boolean,
    onDismiss: () -> Unit,
    onRemoveFollowerClick: () -> Unit,
    onBlockToggleClick: () -> Unit,
    onReportClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val customColors = LocalCustomColors.current

    fun closeSheet(action: () -> Unit) {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            action()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = customColors.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = customColors.textTertiary) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 8.dp)
        ) {
            if (isFollower) {
                BottomSheetActionRow(
                    icon = Icons.Default.PersonRemove,
                    text = stringResource(Res.string.profile_action_remove_follower),
                    tint = customColors.textPrimary,
                    onClick = { closeSheet { onRemoveFollowerClick() } }
                )
            }

            BottomSheetActionRow(
                icon = if (isBlockedByMe) Icons.Default.LockOpen else Icons.Default.Block,
                text = if (isBlockedByMe)
                    stringResource(Res.string.profile_action_unblock)
                else
                    stringResource(Res.string.profile_action_block),
                tint = if (isBlockedByMe) customColors.textPrimary else customColors.red,
                onClick = { closeSheet { onBlockToggleClick() } }
            )

            BottomSheetActionRow(
                icon = Icons.Default.Report,
                text = stringResource(Res.string.profile_action_report),
                tint = customColors.red,
                onClick = { closeSheet { onReportClick() } }
            )
        }
    }
}
@Composable
private fun BottomSheetActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(color = tint, fontWeight = FontWeight.Medium)
        )
    }
}
@Composable
fun ProfileOptionsMenu(
    isBlockedByMe: Boolean,
    isFollower: Boolean,
    onBlockToggleClick: () -> Unit,
    onRemoveFollowerClick: () -> Unit,
    onReportClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isWideScreen = LocalIsWideScreen.current
    val customColors = LocalCustomColors.current

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Seçenekler",
                tint = Color.White
            )
        }

        if (isWideScreen) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = customColors.surface,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 6.dp
            ) {
                if (isFollower) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(Res.string.profile_action_remove_follower),
                                color = customColors.textPrimary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PersonRemove,
                                contentDescription = null,
                                tint = customColors.textSecondary
                            )
                        },
                        onClick = {
                            expanded = false
                            onRemoveFollowerClick()
                        }
                    )
                }

                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(
                                if (isBlockedByMe) Res.string.profile_action_unblock
                                else Res.string.profile_action_block
                            ),
                            color = if (isBlockedByMe) customColors.textPrimary else customColors.red
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isBlockedByMe) Icons.Default.LockOpen else Icons.Default.Block,
                            contentDescription = null,
                            tint = if (isBlockedByMe) customColors.textSecondary else customColors.red
                        )
                    },
                    onClick = {
                        expanded = false
                        onBlockToggleClick()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(Res.string.profile_action_report),
                            color = customColors.red
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = null,
                            tint = customColors.red
                        )
                    },
                    onClick = {
                        expanded = false
                        onReportClick()
                    }
                )
            }
        }
    }

    if (expanded && !isWideScreen) {
        ProfileOptionsBottomSheet(
            isBlockedByMe = isBlockedByMe,
            isFollower = isFollower,
            onDismiss = { expanded = false },
            onBlockToggleClick = {
                expanded = false
                onBlockToggleClick()
            },
            onRemoveFollowerClick = {
                expanded = false
                onRemoveFollowerClick()
            },
            onReportClick = {
                expanded = false
                onReportClick()
            }
        )
    }
}