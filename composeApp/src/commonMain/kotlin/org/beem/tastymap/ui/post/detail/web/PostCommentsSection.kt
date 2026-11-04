package org.beem.tastymap.ui.post.detail.web

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.beem.tastymap.data.model.post.PostResponse
import org.beem.tastymap.ui.post.detail.PlaceInfoCard
import org.beem.tastymap.ui.post.detail.PostCaptionSection
import org.beem.tastymap.ui.post.detail.PostDetailViewModel
import org.beem.tastymap.ui.post.postcomment.CommentListUiState
import org.beem.tastymap.ui.post.postcomment.CommentViewModel
import org.beem.tastymap.ui.post.postcomment.components.CommentItem
import org.beem.tastymap.ui.theme.CustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.be_first_to_comment
import tastymap.composeapp.generated.resources.comments_disabled_label_tr
import tastymap.composeapp.generated.resources.comments_disabled_msg_tr
@Composable
 fun PostCommentsSection(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    post: PostResponse,
    commentUiState: CommentListUiState,
    customColors: CustomColors,
    screenModel: PostDetailViewModel,
    commentViewModel: CommentViewModel,
    postId: Long,
    onUserClick: (Long) -> Unit,
    onReplyClick: (Long, String) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onUpdateClick: (Long) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        item {
            if (!post.placeName.isNullOrBlank()) {
                PlaceInfoCard(post = post)
            }
            PostCaptionSection(post = post)
            Spacer(modifier = Modifier.height(16.dp))
        }

        val isError = !commentUiState.errorMessage.isNullOrBlank()
        val isEmpty = commentUiState.items.isEmpty()

        when {
            !post.isCommentEnabled -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp, horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text =stringResource(Res.string.comments_disabled_label_tr),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = customColors.textPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(Res.string.comments_disabled_msg_tr),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = customColors.textSecondary
                            )
                        )
                    }
                }
            }

            commentUiState.isLoading && isEmpty && !isError -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = customColors.gourmetOrange)
                    }
                }
            }

            isError && isEmpty -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FilledTonalButton(
                            onClick = {
                                commentViewModel.loadInitialData(postId)
                            },
                            enabled = !commentUiState.isLoading,
                            modifier = Modifier.height(48.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = customColors.surfaceVariant,
                                contentColor = customColors.textPrimary
                            )
                        ) {
                            AnimatedContent(
                                targetState = commentUiState.isLoading,
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
            }

            !isError && isEmpty -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.be_first_to_comment),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = customColors.textSecondary
                            )
                        )
                    }
                }
            }

            else -> {
                items(
                    commentUiState.items,
                    key = { it.id }
                ) { comment ->
                    CommentItem(
                        comment = comment,
                        uiState = commentUiState,
                        isWeb = true,
                        isOwnPost = screenModel.isMe(post.userId),
                        checkIsOwnComment = { userId -> screenModel.isMe(userId) },
                        onUserClick = { onUserClick(comment.userId) },
                        onToggleReplies = { commentViewModel.toggleReplies(postId, comment.id) },
                        onLoadMoreReplies = { commentViewModel.loadNextRepliesPage(postId, comment.id) },
                        onReplyClick = onReplyClick,
                        onToggleLike = { id -> commentViewModel.toggleLike(id, postId) },
                        onDeleteClick = onDeleteClick,
                        onPinClick = { id -> commentViewModel.togglePin(id, postId) },
                        onUpdateClick = onUpdateClick
                    )
                }
            }
        }

        if (post.isCommentEnabled && commentUiState.isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = customColors.gourmetOrange
                    )
                }
            }
        } else if (post.isCommentEnabled && commentUiState.loadingMoreError) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {
                            commentViewModel.loadNextPage(postId)
                        }
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
