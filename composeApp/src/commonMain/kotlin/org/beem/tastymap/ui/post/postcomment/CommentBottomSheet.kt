package org.beem.tastymap.ui.post.postcomment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.beem.tastymap.core.util.formatToRelativeDateTime
import org.beem.tastymap.data.model.postcomment.CommentsResponse
import org.beem.tastymap.ui.components.CommentInputBar
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.be_first_to_comment
import tastymap.composeapp.generated.resources.comments_title
import tastymap.composeapp.generated.resources.hide_replies
import tastymap.composeapp.generated.resources.reply_action
import tastymap.composeapp.generated.resources.view_more_replies
import tastymap.composeapp.generated.resources.view_replies
import tastymap.composeapp.generated.resources.view_replies_count

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    postId: Long,
    isCommentEnabled: Boolean,
    screenModel: CommentScreenModel,
    onDismiss: () -> Unit,
    onUserClick: (Long) -> Unit
) {
    val uiState by screenModel.uiState.collectAsState()
    val customColors = LocalCustomColors.current
    val listState = rememberLazyListState()

    var commentText by remember { mutableStateOf("") }
    var isSendingComment by remember { mutableStateOf(false) }
    val currentUserProfileUrl = screenModel.getCurrentUserProfileUrl()

    LaunchedEffect(postId) {
        if(isCommentEnabled) {
            screenModel.loadInitialData(postId)
        }
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            !uiState.isLoading && uiState.errorMessage == null && totalItems > 0 && lastVisibleItem >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            screenModel.loadNextPage(postId)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = customColors.background,
        dragHandle = { BottomSheetDefaults.DragHandle(color = customColors.placeHolderIcon) },
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // BAŞLIK (HEADER)
            Text(
                text = stringResource(Res.string.comments_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = customColors.textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            HorizontalDivider(color = customColors.surfaceVariant)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (!isCommentEnabled) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 48.dp, horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Yorumlar Kapalı",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = customColors.textPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bu gönderi yorumlara kapatılmıştır.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                        )
                    }
                } else if (uiState.isLoading && uiState.items.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = customColors.gourmetOrange)
                    }
                } else if (uiState.items.isEmpty()) {
                    // BOŞ LİSTE UI
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(Res.string.be_first_to_comment),
                            style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(uiState.items, key = { it.id }) { comment ->
                            CommentItem(
                                comment = comment,
                                uiState = uiState,
                                onUserClick = { onUserClick(comment.userId) },
                                onToggleReplies = { screenModel.toggleReplies(postId, comment.id) },
                                onLoadMoreReplies = { screenModel.loadNextRepliesPage(postId, comment.id) }
                            )
                        }

                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = customColors.gourmetOrange
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (isCommentEnabled) {
                CommentInputBar(
                    text = commentText,
                    onTextChange = { commentText = it },
                    onSendClick = {
                        isSendingComment = true
                        screenModel.addComment(
                            postId, commentText,
                            onSuccess = {
                                commentText = ""
                                isSendingComment = false
                            })
                    },
                    isLoading = isSendingComment,
                    profilePhotoUrl = currentUserProfileUrl
                )
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentsResponse,
    uiState: CommentListUiState,
    onUserClick: () -> Unit,
    onToggleReplies: () -> Unit,
    onLoadMoreReplies: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val isRepliesExpanded = uiState.expandedComments.contains(comment.id)
    val replyState = uiState.repliesState[comment.id]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(customColors.surfaceVariant)
                    .clickable { onUserClick() },
                contentAlignment = Alignment.Center
            ) {
                if (!comment.profilePhotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = comment.profilePhotoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = customColors.placeHolderIcon,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.username,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = customColors.textPrimary
                        ),
                        modifier = Modifier.clickable { onUserClick() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatToRelativeDateTime(comment.date),
                        style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary)
                    )
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
                        modifier = Modifier.clickable { /* TODO */ }
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
                                stringResource(Res.string.view_replies_count, comment.replyCount)
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
                IconButton(
                    onClick = { /* TODO: Toggle Like işlemi */ },
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
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = customColors.gourmetOrange,
                        strokeWidth = 2.dp
                    )
                } else {
                    replyState?.items?.forEach { reply ->
                        ReplyItem(reply = reply, onUserClick = { onUserClick() })
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (replyState?.isLastPage == false) {
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

@Composable
private fun ReplyItem(
    reply: CommentsResponse,
    onUserClick: () -> Unit
) {
    val customColors = LocalCustomColors.current
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(customColors.surfaceVariant)
                .clickable { onUserClick() },
            contentAlignment = Alignment.Center
        ) {
            if (!reply.profilePhotoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = reply.profilePhotoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = customColors.placeHolderIcon,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = reply.username,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = customColors.textPrimary
                    ),
                    modifier = Modifier.clickable { onUserClick() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = reply.date,
                    style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = reply.contents,
                style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textPrimary)
            )
        }
    }
}