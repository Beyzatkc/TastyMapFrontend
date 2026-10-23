package org.beem.tastymap.ui.post.detail.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import org.beem.tastymap.data.model.postcomment.CommentRequest
import org.beem.tastymap.ui.components.DialogConfig
import org.beem.tastymap.ui.components.LoadingOverlay
import org.beem.tastymap.ui.components.TastyConfirmDialog
import org.beem.tastymap.ui.components.TastyInputDialog
import org.beem.tastymap.ui.post.detail.PostDetailViewModel
import org.beem.tastymap.ui.post.detail.PostDetailUiState
import org.beem.tastymap.ui.post.postcomment.CommentListUiState
import org.beem.tastymap.ui.post.postcomment.CommentViewModel
import org.beem.tastymap.ui.post.postlike.PostLikesBottomSheet
import org.beem.tastymap.ui.post.postlike.PostLikesViewModel
import org.beem.tastymap.ui.post.postlike.PostLikesUiState
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.delete_comment_confirmation
import tastymap.composeapp.generated.resources.delete_comment_title
import tastymap.composeapp.generated.resources.delete_post_message
import tastymap.composeapp.generated.resources.delete_post_title
import tastymap.composeapp.generated.resources.deleting_post
import tastymap.composeapp.generated.resources.edit_post
import tastymap.composeapp.generated.resources.loading_deleting_comment
import tastymap.composeapp.generated.resources.loading_updating_comment
import tastymap.composeapp.generated.resources.profile_action_reject
import tastymap.composeapp.generated.resources.update_comment_action_en


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
    likesScreenModel: PostLikesViewModel,
    screenModel: PostDetailViewModel,
    commentViewModel: CommentViewModel,
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
                title = stringResource(Res.string.delete_comment_title),
                message =stringResource(Res.string.delete_comment_confirmation),
                confirmText = stringResource(Res.string.profile_action_reject),
                isDestructive = true,
                onConfirm = {
                    commentViewModel.delete(commentId = commentToDelete, postId = postId)
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
            title = stringResource(Res.string.update_comment_action_en),
            initialText = initialText,
            confirmText = stringResource(Res.string.edit_post),
            onConfirm = { newText ->
                val request = CommentRequest(newText)
                commentViewModel.update(
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
        LoadingOverlay(message = stringResource(Res.string.loading_deleting_comment))
    }
    if (commentUiState.isActionLoadingEdit) {
        LoadingOverlay(message = stringResource(Res.string.loading_updating_comment))
    }
}