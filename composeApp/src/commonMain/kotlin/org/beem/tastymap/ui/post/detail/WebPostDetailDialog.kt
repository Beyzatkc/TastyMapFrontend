package org.beem.tastymap.ui.post.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.beem.tastymap.ui.post.postlike.PostLikesViewModel
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.post.detail.web.PostCommentsSection
import org.beem.tastymap.ui.post.detail.web.PostDetailDialogsAndOverlays
import org.beem.tastymap.ui.post.detail.web.PostFooterSection
import org.beem.tastymap.ui.post.detail.web.PostHeaderWithOptions
import org.beem.tastymap.ui.post.postcomment.CommentViewModel
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import org.beem.tastymap.ui.bottomnav.ProfileTabRoute
import org.beem.tastymap.ui.post.EditPostRoute
import org.beem.tastymap.ui.profile.ProfileRoute
import tastymap.composeapp.generated.resources.*

@Composable
fun WebPostDetailDialog(
    postId: Long,
    screenModel: PostDetailViewModel,
    likesScreenModel: PostLikesViewModel,
    commentViewModel: CommentViewModel,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val uiState by screenModel.uiState.collectAsState()
    val likeUiState by likesScreenModel.uiState.collectAsState()
    val commentUiState by commentViewModel.uiState.collectAsState()

    var showPostLikeSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var commentToDelete by remember { mutableStateOf<Long?>(null) }
    var commentToUpdate by remember { mutableStateOf<Long?>(null) }

    var commentText by remember { mutableStateOf("") }
    var isSendingComment by remember { mutableStateOf(false) }
    var replyingToCommentId by remember { mutableStateOf<Long?>(null) }
    var replyingToUsername by remember { mutableStateOf<String?>(null) }

    val customColors = LocalCustomColors.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val currentUserProfileUrl = commentViewModel.getCurrentUserProfileUrl()

    val navigateToProfile: (Long) -> Unit = { targetUserId ->
        onDismiss()
        if (screenModel.isMe(targetUserId)) {
            navController.navigate(ProfileTabRoute) {
                popUpTo(navController.graph.findStartDestination().route!!) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            navController.navigate(ProfileRoute(userId = targetUserId, username = uiState.post?.username?:"Kullanıcı"))
        }
    }

    val shouldLoadMoreComments = remember(uiState.post?.isCommentEnabled, commentUiState.isLoading) {
        derivedStateOf {
            val isCommentEnabled = uiState.post?.isCommentEnabled == true
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            isCommentEnabled &&
                    !uiState.isFetchPost &&
                    !commentUiState.isLoading &&
                    !commentUiState.isLoadingMore &&
                    !commentUiState.isLastPage &&
                    commentUiState.errorMessage == null &&
                    totalItems > 0 &&
                    lastVisibleItem >= totalItems - 2
        }
    }

    LaunchedEffect(key1 = postId) {
        commentViewModel.clearState()
        screenModel.loadPostDetail(postId)
        screenModel.fetchRemotePost(postId)
    }
    LaunchedEffect(uiState.isFetchPost) {
        val currentPost = uiState.post
        if (uiState.isFetchPost) {
            currentPost?.let {
                commentViewModel.syncCommentsForPost(
                    postId = postId,
                    isCommentEnabled = it.isCommentEnabled
                )
            }
        }
    }

    LaunchedEffect(commentUiState.errorMessage) {
        if (!commentUiState.errorMessage.isNullOrBlank()) {
            delay(2000L)
            commentViewModel.clearError()
        }
    }
    LaunchedEffect(likeUiState.errorMessage) {
        if (!likeUiState.errorMessage.isNullOrBlank()) {
            delay(2000L)
            likesScreenModel.clearError()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (!uiState.errorMessage.isNullOrBlank()) {
            delay(2000L)
            screenModel.clearError()
        }
    }

    LaunchedEffect(shouldLoadMoreComments.value && uiState.isFetchPost) {
        if (shouldLoadMoreComments.value) {
            commentViewModel.loadNextPage(postId)
        }
    }

    LaunchedEffect(uiState.isDeletedSuccessfully) {
        if (uiState.isDeletedSuccessfully) {
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = {
            screenModel.resetState()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 1200.dp)
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = customColors.background,
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val post = uiState.post

                when {
                    uiState.isLoading && post == null || post?.postId != postId -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(color = customColors.gourmetOrange)
                        }
                    }

                    !post.username.isNullOrEmpty() -> {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .fillMaxHeight()
                                    .background(customColors.background),
                                contentAlignment = Alignment.Center
                            ) {
                                PostMediaPager(
                                    photoUrls = post.photoUrls,
                                    onDoubleTapLike = {
                                        if (!post.isLiked) {
                                            screenModel.toggleLike(postId)
                                        }
                                    }
                                )
                            }

                            VerticalDivider(
                                color = customColors.surfaceVariant.copy(alpha = 0.5f),
                                thickness = 1.dp
                            )

                            Column(
                                modifier = Modifier
                                    .width(380.dp)
                                    .fillMaxHeight()
                                    .background(customColors.background),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                PostHeaderWithOptions(
                                    post = post,
                                    isOwnPost = uiState.isOwnPost,
                                    showMenu = showMenu,
                                    customColors = customColors,
                                    onUserClick = { navigateToProfile(post.userId) },
                                    onMenuToggle = { showMenu = it },
                                    onPinClick = { screenModel.togglePin(postId) },
                                    onEditClick = {
                                        onDismiss()
                                        // YENİ: navigator.push yerine navigate
                                        navController.navigate(EditPostRoute(postId))
                                    },
                                    onDeleteClick = { showDeleteDialog = true }
                                )

                                PostCommentsSection(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    listState = listState,
                                    post = post,
                                    commentUiState = commentUiState,
                                    customColors = customColors,
                                    screenModel = screenModel,
                                    commentViewModel = commentViewModel,
                                    postId = postId,
                                    onUserClick = navigateToProfile,
                                    onReplyClick = { parentId, username ->
                                        replyingToCommentId = parentId
                                        replyingToUsername = username
                                    },
                                    onDeleteClick = { commentToDelete = it },
                                    onUpdateClick = { commentToUpdate = it }
                                )

                                PostFooterSection(
                                    post = post,
                                    customColors = customColors,
                                    commentText = commentText,
                                    replyingToUsername = replyingToUsername,
                                    isSendingComment = isSendingComment,
                                    currentUserProfileUrl = currentUserProfileUrl,
                                    onLikeClick = { screenModel.toggleLike(postId) },
                                    onLikeCountClick = { showPostLikeSheet = true },
                                    onCommentTextChange = { commentText = it },
                                    onCancelReply = {
                                        replyingToCommentId = null
                                        replyingToUsername = null
                                    },
                                    onSendComment = {
                                        isSendingComment = true
                                        if (replyingToCommentId != null) {
                                            val finalReplyText = "@$replyingToUsername $commentText"
                                            commentViewModel.addReply(
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
                                            commentViewModel.addComment(
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
                                    }
                                )
                            }
                        }
                    }

                    !uiState.isLoading && uiState.errorMessage != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            FilledTonalButton(
                                onClick = { screenModel.fetchRemotePost(postId) },
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
                }

                val activeErrorMessage = commentUiState.errorMessage
                    ?: likeUiState.errorMessage
                    ?: uiState.errorMessage

                AnimatedVisibility(
                    visible = !activeErrorMessage.isNullOrBlank(),
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                ) {
                    activeErrorMessage?.let { msg ->
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
            }
        }
    }

    PostDetailDialogsAndOverlays(
        postId = postId,
        showPostLikeSheet = showPostLikeSheet,
        showDeleteDialog = showDeleteDialog,
        commentToDelete = commentToDelete,
        commentToUpdate = commentToUpdate,
        uiState = uiState,
        likeUiState = likeUiState,
        commentUiState = commentUiState,
        likesScreenModel = likesScreenModel,
        screenModel = screenModel,
        commentViewModel = commentViewModel,
        onDismissLikeSheet = { showPostLikeSheet = false },
        onDismissDeleteDialog = { showDeleteDialog = false },
        onDismissCommentDelete = { commentToDelete = null },
        onDismissCommentUpdate = { commentToUpdate = null },
        onNavigateToUser = navigateToProfile
    )
}