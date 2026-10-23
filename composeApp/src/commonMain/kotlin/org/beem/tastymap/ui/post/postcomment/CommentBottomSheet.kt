package org.beem.tastymap.ui.post.postcomment

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.beem.tastymap.data.model.postcomment.CommentRequest
import org.beem.tastymap.ui.components.CommentInputBar
import org.beem.tastymap.ui.components.DialogConfig
import org.beem.tastymap.ui.components.LoadingOverlay
import org.beem.tastymap.ui.components.TastyConfirmDialog
import org.beem.tastymap.ui.components.TastyInputDialog
import org.beem.tastymap.ui.post.postcomment.components.CommentItem
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.be_first_to_comment
import tastymap.composeapp.generated.resources.comments_disabled_label_tr
import tastymap.composeapp.generated.resources.comments_disabled_msg_tr
import tastymap.composeapp.generated.resources.comments_title
import tastymap.composeapp.generated.resources.delete_comment_confirmation
import tastymap.composeapp.generated.resources.delete_comment_title
import tastymap.composeapp.generated.resources.edit_post
import tastymap.composeapp.generated.resources.loading_deleting_comment
import tastymap.composeapp.generated.resources.loading_updating_comment
import tastymap.composeapp.generated.resources.profile_action_reject
import tastymap.composeapp.generated.resources.update_comment_action_tr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    postId: Long,
    isOwnPost: Boolean = false,
    isCommentEnabled: Boolean,
    screenModel: CommentViewModel,
    onDismiss: () -> Unit,
    onUserClick: (Long) -> Unit
) {
    val uiState by screenModel.uiState.collectAsState()
    val customColors = LocalCustomColors.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var commentToDelete by remember { mutableStateOf<Long?>(null) }
    var commentToUpdate by remember { mutableStateOf<Long?>(null) }

    var commentText by remember { mutableStateOf("") }
    var isSendingComment by remember { mutableStateOf(false) }
    val currentUserProfileUrl = screenModel.getCurrentUserProfileUrl()

    var replyingToCommentId by remember { mutableStateOf<Long?>(null) }
    var replyingToUsername by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(uiState.errorMessage) {
        if (!uiState.errorMessage.isNullOrBlank()) {
            delay(2000L)
            screenModel.clearError()
        }
    }

    LaunchedEffect(postId, isCommentEnabled) {
        screenModel.syncCommentsForPost(postId, isCommentEnabled)
    }

    val shouldLoadMore = remember(isCommentEnabled) {
        derivedStateOf {
            if (!isCommentEnabled) return@derivedStateOf false
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            !uiState.isLoadingMore &&
                    !uiState.isLastPage &&
                    !uiState.isLoading &&
                    uiState.errorMessage == null &&
                    totalItems > 0 &&
                    lastVisibleItem >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            screenModel.loadNextPage(postId)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = customColors.background,
        dragHandle = { BottomSheetDefaults.DragHandle(color = customColors.placeHolderIcon) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .fillMaxHeight(0.75f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
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
                    val isError = !uiState.errorMessage.isNullOrBlank()
                    val isEmpty = uiState.items.isEmpty()

                    when {
                        !isCommentEnabled -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 48.dp, horizontal = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(Res.string.comments_disabled_label_tr),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = customColors.textPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(Res.string.comments_disabled_msg_tr),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                                )
                            }
                        }

                        uiState.isLoading && isEmpty -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = customColors.gourmetOrange)
                            }
                        }

                        isError && isEmpty -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                FilledTonalButton(
                                    onClick = {
                                        if (isCommentEnabled) {
                                            screenModel.loadInitialData(postId)
                                        }
                                    },
                                    enabled = !uiState.isLoading,
                                    modifier = Modifier.height(48.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = customColors.surfaceVariant,
                                        contentColor = customColors.textPrimary
                                    )
                                ) {
                                    AnimatedContent(
                                        targetState = uiState.isLoading,
                                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                                        label = "ButtonLoadingTransition"
                                    ) { loading ->
                                        if (loading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.5.dp,
                                                color = customColors.textPrimary
                                            )
                                        } else {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = stringResource(Res.string.active_devices_retry_cd),
                                                    modifier = Modifier.size(24.dp),
                                                    tint = customColors.textSecondary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = stringResource(Res.string.active_devices_retry),
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = customColors.textSecondary
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        isEmpty -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(Res.string.be_first_to_comment),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                                )
                            }
                        }

                        else -> {
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
                                        isWeb = false,
                                        isOwnPost = isOwnPost,
                                        checkIsOwnComment = { userId -> screenModel.isMe(userId) },

                                        onToggleReplies = {
                                            screenModel.toggleReplies(postId, comment.id)
                                        },
                                        onLoadMoreReplies = {
                                            screenModel.loadNextRepliesPage(postId, comment.id)
                                        },
                                        onReplyClick = { parentId, username ->
                                            replyingToCommentId = parentId
                                            replyingToUsername = username
                                        },
                                        onToggleLike = { id ->
                                            screenModel.toggleLike(id, postId)
                                        },
                                        onDeleteClick = { commentId ->
                                            commentToDelete = commentId
                                        },
                                        onPinClick = { id ->
                                            screenModel.togglePin(id, postId)
                                        },
                                        onUpdateClick = { commentId ->
                                            commentToUpdate = commentId
                                        }
                                    )
                                }

                                if (isCommentEnabled && uiState.isLoadingMore) {
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
                                } else if (isCommentEnabled && uiState.loadingMoreError) {
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            IconButton(
                                                onClick = { screenModel.loadNextPage(postId) }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = stringResource(Res.string.active_devices_retry),
                                                    tint = customColors.textSecondary,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                            Text(
                                                text = stringResource(Res.string.active_devices_retry),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = customColors.textSecondary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
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
                        replyingToUsername = replyingToUsername,
                        onCancelReply = {
                            replyingToCommentId = null
                            replyingToUsername = null
                        },
                        onSendClick = {
                            isSendingComment = true
                            if (replyingToCommentId != null) {
                                val finalReplyText = "@$replyingToUsername $commentText"
                                screenModel.addReply(
                                    postId = postId,
                                    parentCommentId = replyingToCommentId!!,
                                    contents = finalReplyText,
                                    onSuccess = {
                                        commentText = ""
                                        replyingToCommentId = null
                                        replyingToUsername = null
                                        isSendingComment = false
                                    }
                                )
                            } else {
                                screenModel.addComment(
                                    postId = postId,
                                    contents = commentText,
                                    onSuccess = {
                                        commentText = ""
                                        isSendingComment = false
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(0)
                                        }
                                    }
                                )
                            }
                        },
                        isLoading = isSendingComment,
                        profilePhotoUrl = currentUserProfileUrl
                    )
                }
            }
            this@ModalBottomSheet.AnimatedVisibility(
                visible = !uiState.errorMessage.isNullOrBlank() && uiState.items.isNotEmpty(),
                enter = fadeIn() + slideInVertically(initialOffsetY = { height: Int -> height / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { height: Int -> height / 2 }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom = if (isCommentEnabled) 80.dp else 24.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {
                uiState.errorMessage?.let { msg ->
                    Surface(
                        color = customColors.surface,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 8.dp,
                        border = BorderStroke(1.dp, customColors.borderLight)
                    ) {
                        Text(
                            text = msg,
                            color = customColors.textPrimary,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            if (commentToDelete != null) {
                TastyConfirmDialog(
                    config = DialogConfig(
                        title = stringResource(Res.string.delete_comment_title),
                        message = stringResource(Res.string.delete_comment_confirmation),
                        confirmText = stringResource(Res.string.profile_action_reject),
                        isDestructive = true,
                        onConfirm = {
                            screenModel.delete(commentId = commentToDelete!!, postId = postId)
                            commentToDelete = null
                        }
                    ),
                    onDismiss = { commentToDelete = null }
                )
            }

            if (commentToUpdate != null) {
                val initialText = remember(commentToUpdate, uiState) {
                    val id = commentToUpdate
                    val mainComment = uiState.items.find { it.id == id }
                    if (mainComment != null) return@remember mainComment.contents

                    var replyText = ""
                    for (replyState in uiState.repliesState.values) {
                        val reply = replyState.items.find { it.id == id }
                        if (reply != null) {
                            replyText = reply.contents
                            break
                        }
                    }
                    replyText
                }

                TastyInputDialog(
                    title = stringResource(Res.string.update_comment_action_tr),
                    initialText = initialText,
                    confirmText =stringResource(Res.string.edit_post),
                    onConfirm = { newText ->
                        val request = CommentRequest(newText)
                        screenModel.update(
                            commentId = commentToUpdate!!,
                            postId = postId,
                            request = request
                        )
                        commentToUpdate = null
                    },
                    onDismiss = { commentToUpdate = null }
                )
            }

            if (uiState.isActionLoadingDelete) {
                LoadingOverlay(message = stringResource(Res.string.loading_deleting_comment))
            }

            if (uiState.isActionLoadingEdit) {
                LoadingOverlay(message = stringResource(Res.string.loading_updating_comment))
            }
        }
    }
}