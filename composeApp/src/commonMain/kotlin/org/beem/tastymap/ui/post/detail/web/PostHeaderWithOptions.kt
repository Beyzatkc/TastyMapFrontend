package org.beem.tastymap.ui.post.detail.web

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beem.tastymap.data.model.post.PostResponse
import org.beem.tastymap.ui.post.detail.PostHeader
import org.beem.tastymap.ui.theme.CustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.delete_post
import tastymap.composeapp.generated.resources.edit_post
import tastymap.composeapp.generated.resources.options
import tastymap.composeapp.generated.resources.pin_post
import tastymap.composeapp.generated.resources.unpin_post


@Composable
fun PostHeaderWithOptions(
    post: PostResponse,
    isOwnPost: Boolean,
    showMenu: Boolean,
    customColors: CustomColors,
    onUserClick: () -> Unit,
    onMenuToggle: (Boolean) -> Unit,
    onPinClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                PostHeader(
                    post = post,
                    onUserClick = onUserClick
                )
            }

            if (isOwnPost && !post.username.isNullOrEmpty()) {
                Box(modifier = Modifier.padding(end = 8.dp)) {
                    IconButton(onClick = { onMenuToggle(true) }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(Res.string.options),
                            tint = customColors.textPrimary,
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        containerColor = customColors.surface,
                        onDismissRequest = { onMenuToggle(false) }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (post.isPinned) stringResource(Res.string.unpin_post)
                                    else stringResource(Res.string.pin_post),
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
                                onMenuToggle(false)
                                onPinClick()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(Res.string.edit_post),
                                    color = customColors.textPrimary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = customColors.textPrimary
                                )
                            },
                            onClick = {
                                onMenuToggle(false)
                                onEditClick()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(Res.string.delete_post),
                                    color = customColors.red
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = customColors.red
                                )
                            },
                            onClick = {
                                onMenuToggle(false)
                                onDeleteClick()
                            }
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = customColors.surfaceVariant.copy(alpha = 0.5f))
    }
}
