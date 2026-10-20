package org.beem.tastymap.ui.post.detail.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import org.beem.tastymap.data.model.postcomment.CommentRequest
import org.beem.tastymap.ui.components.DialogConfig
import org.beem.tastymap.ui.components.LoadingOverlay
import org.beem.tastymap.ui.components.TastyConfirmDialog
import org.beem.tastymap.ui.components.TastyInputDialog
import org.beem.tastymap.ui.post.detail.PostDetailScreenModel
import org.beem.tastymap.ui.post.detail.PostDetailUiState
import org.beem.tastymap.ui.post.postcomment.CommentListUiState
import org.beem.tastymap.ui.post.postcomment.CommentScreenModel
import org.beem.tastymap.ui.post.postlike.PostLikesBottomSheet
import org.beem.tastymap.ui.post.postlike.PostLikesScreenModel
import org.beem.tastymap.ui.post.postlike.PostLikesUiState
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.delete_post_message
import tastymap.composeapp.generated.resources.delete_post_title
import tastymap.composeapp.generated.resources.deleting_post
import tastymap.composeapp.generated.resources.profile_action_reject


@Composable
 fun PostDetailDialogsAndOverlays(
    postId: Long,
    showPostLikeSheet: Boolean,
    showDeleteDialog: Boolean,
    commentToDelete: Long?,
    commentToUpdate: Long?,
    uiState: PostDetailUiState,
    likeUiState: PostLikesUiState,
    commentUiState: CommentListUiState,
    likesScreenModel: PostLikesScreenModel,
    screenModel: PostDetailScreenModel,
    commentScreenModel: CommentScreenModel,
    onDismissLikeSheet: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onDismissCommentDelete: () -> Unit,
    onDismissCommentUpdate: () -> Unit,
    onNavigateToUser: (Long) -> Unit
) {

    if (showPostLikeSheet) {
        LaunchedEffect(postId) {
            likesScreenModel.loadInitialData(postId)
        }
        PostLikesBottomSheet(
            onDismiss = onDismissLikeSheet,
            onClear = {
                screenModel.clearError()
            },
            onUserClick = { selectedUserId ->
                onDismissLikeSheet()
                onNavigateToUser(selectedUserId)
            },
            uiState = likeUiState,
            onActionClick = { selectedUserId, status ->
                likesScreenModel.handleFollowAction(selectedUserId, status)
            },
            onLoadNextPage = { likesScreenModel.loadNextPage(postId) },
            onRetry = { likesScreenModel.loadInitialData(postId) }
        )
    }

    if (showDeleteDialog) {
        TastyConfirmDialog(
            config = DialogConfig(
                title = stringResource(Res.string.delete_post_title),
                message = stringResource(Res.string.delete_post_message),
                confirmText = stringResource(Res.string.profile_action_reject),
                isDestructive = true,
                onConfirm = { screenModel.deletePost(postId) }
            ),
            onDismiss = onDismissDeleteDialog
        )
    }


    if (commentToDelete != null) {
        TastyConfirmDialog(
            config = DialogConfig(
                title = "Yorumu Sil",
                message = "Bu yorumu silmek istediğinize emin misiniz?",
                confirmText = stringResource(Res.string.profile_action_reject),
                isDestructive = true,
                onConfirm = {
                    commentScreenModel.delete(commentId = commentToDelete, postId = postId)
                    onDismissCommentDelete()
                }
            ),
            onDismiss = onDismissCommentDelete
        )
    }

    if (commentToUpdate != null) {
        val initialText = remember(commentToUpdate, commentUiState) {
            val id = commentToUpdate
            val mainComment = commentUiState.items.find { it.id == id }
            if (mainComment != null) return@remember mainComment.contents

            var replyText = ""
            for (replyState in commentUiState.repliesState.values) {
                val reply = replyState.items.find { it.id == id }
                if (reply != null) {
                    replyText = reply.contents
                    break
                }
            }
            replyText
        }

        TastyInputDialog(
            title = "Yorumu Güncelle",
            initialText = initialText,
            confirmText = "Güncelle",
            onConfirm = { newText ->
                val request = CommentRequest(newText)
                commentScreenModel.update(
                    commentId = commentToUpdate,
                    postId = postId,
                    request = request
                )
                onDismissCommentUpdate()
            },
            onDismiss = onDismissCommentUpdate
        )
    }

    if (uiState.isActionLoadingDelete) {
        LoadingOverlay(message = stringResource(Res.string.deleting_post))
    }
    if (uiState.isActionLoadingPin) {
        LoadingOverlay(message = null)
    }
    if (commentUiState.isActionLoadingDelete) {
        LoadingOverlay(message = "Yorum siliniyor...")
    }
    if (commentUiState.isActionLoadingEdit) {
        LoadingOverlay(message = "Yorum güncelleniyor...")
    }
}