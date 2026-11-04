package org.beem.tastymap.ui.post.postcomment.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.beem.tastymap.core.util.formatToShortRelativeTime
import org.beem.tastymap.data.model.postcomment.CommentsResponse
import org.beem.tastymap.ui.post.postcomment.CommentListUiState
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.comment_edited
import tastymap.composeapp.generated.resources.content_desc_pinned
import tastymap.composeapp.generated.resources.hide_replies
import tastymap.composeapp.generated.resources.options
import tastymap.composeapp.generated.resources.reply_action
import tastymap.composeapp.generated.resources.view_more_replies
import tastymap.composeapp.generated.resources.view_replies_count

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentItem(
    comment: CommentsResponse,
    uiState: CommentListUiState,
    isWeb: Boolean,
    isOwnPost: Boolean = false,
    checkIsOwnComment: (Long) -> Boolean,
    onUserClick: () -> Unit,
    onToggleReplies: () -> Unit,
    onLoadMoreReplies: () -> Unit,
    onToggleLike: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onPinClick: (Long) -> Unit,
    onUpdateClick: (Long) -> Unit,
    onReplyClick: (Long, String) -> Unit
) {
    val customColors = LocalCustomColors.current
    val isRepliesExpanded = uiState.expandedComments.contains(comment.id)
    val replyState = uiState.repliesState[comment.id]

    var showMenu by remember { mutableStateOf(false) }
    val isOwnComment = checkIsOwnComment(comment.userId)

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    var baseModifier = Modifier.fillMaxWidth()
    if (!isWeb) {
        baseModifier = baseModifier.combinedClickable(
            onLongClick = { showMenu = true },
            onClick = {}
        )
    } else {
        baseModifier = baseModifier.hoverable(interactionSource)
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = baseModifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(customColors.surfaceVariant).clickable { onUserClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (!comment.profilePhotoUrl.isNullOrBlank()) { AsyncImage(model = comment.profilePhotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop) } else { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = customColors.placeHolderIcon, modifier = Modifier.size(24.dp)) }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = comment.username, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = customColors.textPrimary), modifier = Modifier.clickable { onUserClick() })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = formatToShortRelativeTime(comment.date), style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary))

                        if (comment.updateDate != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(Res.string.comment_edited),
                                style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary),
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        if (isWeb && isHovered) {
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = stringResource(Res.string.options), tint = customColors.textSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = comment.contents,
                        style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textPrimary)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(Res.string.reply_action),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = customColors.textSecondary
                            ),
                            modifier = Modifier.clickable {
                                onReplyClick(comment.id, comment.username)
                            }
                        )
                    }

                    if (comment.replyCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.clickable { onToggleReplies() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.width(24.dp).padding(end = 8.dp),
                                color = customColors.textSecondary.copy(alpha = 0.5f)
                            )
                            Text(
                                text = if (isRepliesExpanded) {
                                    stringResource(Res.string.hide_replies)
                                } else {
                                    stringResource(
                                        Res.string.view_replies_count,
                                        comment.replyCount
                                    )
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = customColors.textSecondary
                                )
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (comment.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = stringResource(Res.string.content_desc_pinned),
                            tint = customColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    IconButton(
                        onClick = { onToggleLike(comment.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (comment.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = if (comment.isLiked) customColors.red else customColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (comment.numberOfLikes > 0) {
                        Text(
                            text = comment.numberOfLikes.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary)
                        )
                    }
                }
            }
            AnimatedVisibility(visible = isRepliesExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 52.dp)
                ) {
                    if (replyState?.isLoading == true && replyState.items.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = customColors.gourmetOrange,
                                strokeWidth = 2.dp
                            )
                        }
                    } else {
                        replyState?.items?.forEach { reply ->
                            ReplyItem(
                                reply = reply,
                                isWeb = isWeb,
                                isOwnPost = isOwnPost,
                                checkIsOwnComment = checkIsOwnComment,
                                onUserClick = { onUserClick() },
                                onReplyClick = { onReplyClick(comment.id, reply.username) },
                                onDeleteClick = { onDeleteClick(reply.id) },
                                onUpdateClick = { onUpdateClick(reply.id) },
                                onToggleLike = { onToggleLike(reply.id) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (!replyState?.errorMessage.isNullOrBlank() && replyState?.items?.isEmpty() == true) {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { onToggleReplies() },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = customColors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(Res.string.active_devices_retry),
                                    style = MaterialTheme.typography.labelMedium.copy(color = customColors.textSecondary)
                                )
                            }
                        }

                        if (replyState?.isLastPage == false && replyState.items.isNotEmpty()) {
                            when {
                                replyState.isLoadingMore -> {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = customColors.gourmetOrange,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }

                                replyState.loadingMoreErrorReply -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onLoadMoreReplies() }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = stringResource(Res.string.active_devices_retry_cd),
                                            tint = customColors.textSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = stringResource(Res.string.active_devices_retry),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = customColors.textSecondary
                                            )
                                        )
                                    }
                                }

                                else -> {
                                    Text(
                                        text = stringResource(Res.string.view_more_replies),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = customColors.textSecondary
                                        ),
                                        modifier = Modifier
                                            .clickable { onLoadMoreReplies() }
                                            .padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        CommentActionDropdownMenu(
            expanded = showMenu,
            isOwnComment = isOwnComment,
            isOwnPost = isOwnPost,
            isPinned = comment.isPinned,
            onDismissRequest = { showMenu = false },
            onPinClick = {
                showMenu = false
                onPinClick(comment.id)
            },
            onUpdateClick = {
                showMenu = false
                onUpdateClick(comment.id)
            },
            onDeleteClick = {
                showMenu = false
                onDeleteClick(comment.id)
            },
            onReportClick = {
                showMenu = false
                // TODO: Şikayet etme dialogu / API isteği eklenecek
            }
        )
    }
}