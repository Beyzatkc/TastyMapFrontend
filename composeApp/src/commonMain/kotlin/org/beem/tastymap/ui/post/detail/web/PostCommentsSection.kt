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
import org.beem.tastymap.ui.post.detail.PostDetailScreenModel
import org.beem.tastymap.ui.post.postcomment.CommentListUiState
import org.beem.tastymap.ui.post.postcomment.CommentScreenModel
import org.beem.tastymap.ui.post.postcomment.components.CommentItem
import org.beem.tastymap.ui.theme.CustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.be_first_to_comment


@Composable
 fun PostCommentsSection(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    post: PostResponse,
    commentUiState: CommentListUiState,
    customColors: CustomColors,
    screenModel: PostDetailScreenModel,
    commentScreenModel: CommentScreenModel,
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
                            text = "Yorumlar Kapalı",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = customColors.textPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bu gönderi yorumlara kapatılmıştır.",
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
                                commentScreenModel.loadInitialData(postId)
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
                        checkIsOwnComment = { userId -> screenModel.isMe(userId) },
                        onUserClick = { onUserClick(comment.userId) },
                        onToggleReplies = { commentScreenModel.toggleReplies(postId, comment.id) },
                        onLoadMoreReplies = { commentScreenModel.loadNextRepliesPage(postId, comment.id) },
                        onReplyClick = onReplyClick,
                        onToggleLike = { id -> commentScreenModel.toggleLike(id, postId) },
                        onDeleteClick = onDeleteClick,
                        onPinClick = { id -> commentScreenModel.togglePin(id, postId) },
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
                            commentScreenModel.loadNextPage(postId)
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
