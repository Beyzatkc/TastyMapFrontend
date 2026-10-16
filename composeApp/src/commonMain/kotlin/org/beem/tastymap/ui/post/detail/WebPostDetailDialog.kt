package org.beem.tastymap.ui.post.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import kotlinx.coroutines.launch
import org.beem.tastymap.core.util.formatToRelativeDateTime
import org.beem.tastymap.ui.bottomnav.ProfileTab
import org.beem.tastymap.ui.components.CommentInputBar
import org.beem.tastymap.ui.post.postlike.PostLikesBottomSheet
import org.beem.tastymap.ui.post.postlike.PostLikesScreenModel
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.components.TastyConfirmDialog
import org.beem.tastymap.ui.components.DialogConfig
import org.beem.tastymap.ui.components.LoadingOverlay
import org.beem.tastymap.ui.post.mypost.updatepost.EditPostScreen
import org.beem.tastymap.ui.post.postcomment.CommentItem
import org.beem.tastymap.ui.post.postcomment.CommentScreenModel
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.be_first_to_comment
import tastymap.composeapp.generated.resources.delete_post
import tastymap.composeapp.generated.resources.delete_post_message
import tastymap.composeapp.generated.resources.delete_post_title
import tastymap.composeapp.generated.resources.deleting_post
import tastymap.composeapp.generated.resources.edit_post
import tastymap.composeapp.generated.resources.options
import tastymap.composeapp.generated.resources.pin_post
import tastymap.composeapp.generated.resources.profile_action_reject
import tastymap.composeapp.generated.resources.unpin_post

@Composable
fun WebPostDetailDialog(
    postId: Long,
    screenModel: PostDetailScreenModel,
    likesScreenModel: PostLikesScreenModel,
    commentScreenModel: CommentScreenModel,
    onDismiss: () -> Unit
) {
    val uiState by screenModel.uiState.collectAsState()
    val likeUiState by likesScreenModel.uiState.collectAsState()
    val commentUiState by commentScreenModel.uiState.collectAsState()

    val currentUserProfileUrl = commentScreenModel.getCurrentUserProfileUrl()

    val coroutineScope = rememberCoroutineScope()

    var commentText by remember { mutableStateOf("") }
    var isSendingComment by remember { mutableStateOf(false) }

    var replyingToCommentId by remember { mutableStateOf<Long?>(null) }
    var replyingToUsername by remember { mutableStateOf<String?>(null) }

    val customColors = LocalCustomColors.current
    val tabNavigator = LocalTabNavigator.current
    val navigator = LocalNavigator.currentOrThrow

    var showPostLikeSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(key1 = postId) {
        screenModel.loadPostDetail(postId)
        screenModel.fetchRemotePost(postId)
    }
    LaunchedEffect(key1 = uiState.post?.isCommentEnabled) {
        if (uiState.post?.isCommentEnabled == true) {
            commentScreenModel.loadInitialData(postId)
        }
    }

    val shouldLoadMoreComments = remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            !commentUiState.isLoading && commentUiState.errorMessage == null && totalItems > 0 && lastVisibleItem >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMoreComments.value) {
        if (shouldLoadMoreComments.value) {
            commentScreenModel.loadNextPage(postId)
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
                .width(1000.dp)
                .height(620.dp),
            shape = RoundedCornerShape(16.dp),
            color = customColors.background,
            shadowElevation = 8.dp
        ) {
            if (uiState.isLoading && uiState.post == null || uiState.post?.postId != postId) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(color = customColors.gourmetOrange)
                }
            } else {
                uiState.post?.let { post ->
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxHeight()
                                .background(Color.Black),
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
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        PostHeader(
                                            post = post,
                                            onUserClick = {
                                                onDismiss()
                                                if (screenModel.isMe(post.userId)) {
                                                    tabNavigator.current = ProfileTab
                                                    navigator.popUntilRoot()
                                                } else {
                                                    navigator.push(ProfileScreen(userId = post.userId))
                                                }
                                            }
                                        )
                                    }

                                    if (uiState.isOwnPost && !post.username.isNullOrEmpty()) {
                                        Box(modifier = Modifier.padding(end = 8.dp)) {
                                            IconButton(onClick = { showMenu = true }) {
                                                Icon(
                                                    imageVector = Icons.Default.MoreVert,
                                                    contentDescription = stringResource(Res.string.options),
                                                    tint = customColors.textPrimary,
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = showMenu,
                                                containerColor = customColors.background,
                                                onDismissRequest = { showMenu = false }
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
                                                        showMenu = false
                                                        screenModel.togglePin(postId)
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
                                                        showMenu = false
                                                        onDismiss()
                                                        navigator.push(EditPostScreen(postId))
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
                                                        showMenu = false
                                                        showDeleteDialog = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                HorizontalDivider(color = customColors.surfaceVariant.copy(alpha = 0.5f))
                            }
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
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
                                                    style = MaterialTheme.typography.titleSmall.copy(color = customColors.textPrimary)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Bu gönderi yorumlara kapatılmıştır.",
                                                    style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                                                )
                                            }
                                        }
                                    }
                                    commentUiState.isLoading && isEmpty && !isError -> {
                                        item {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(color = customColors.gourmetOrange)
                                            }
                                        }
                                    }
                                    isError && isEmpty -> {
                                        item {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                FilledTonalButton(
                                                    onClick = { commentScreenModel.loadInitialData(postId) },
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
                                    isEmpty -> {
                                        item {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = stringResource(Res.string.be_first_to_comment),
                                                    style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                                                )
                                            }
                                        }
                                    }
                                    else -> {
                                        items(commentUiState.items, key = { it.id }) { comment ->
                                            CommentItem(
                                                comment = comment,
                                                uiState = commentUiState,
                                                onUserClick = {
                                                    onDismiss()
                                                    if (screenModel.isMe(comment.userId)) {
                                                        tabNavigator.current = ProfileTab
                                                        navigator.popUntilRoot()
                                                    } else {
                                                        navigator.push(ProfileScreen(userId = comment.userId))
                                                    }
                                                },
                                                onToggleReplies = {
                                                    commentScreenModel.toggleReplies(
                                                        postId,
                                                        comment.id
                                                    )
                                                },
                                                onLoadMoreReplies = {
                                                    commentScreenModel.loadNextRepliesPage(
                                                        postId,
                                                        comment.id
                                                    )
                                                },
                                                onReplyClick = { parentId, username ->
                                                    replyingToCommentId = parentId
                                                    replyingToUsername = username
                                                }
                                            )
                                        }
                                    }
                                }
                                if (post.isCommentEnabled && commentUiState.isLoadingMore) {
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


                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(customColors.background)
                            ) {
                                HorizontalDivider(color = customColors.surfaceVariant.copy(alpha = 0.5f))

                                PostActionBar(
                                    post = post,
                                    onLikeClick = { screenModel.toggleLike(postId) },
                                    onLikeCountClick = { showPostLikeSheet = true },
                                    onCommentClick = { /* Odaklanmayı input box'a yönlendir */ }
                                )

                                Text(
                                    text = formatToRelativeDateTime(post.createdAt),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = customColors.textSecondary.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(
                                    color = customColors.surfaceVariant,
                                    thickness = 1.2.dp
                                )
                                if (post.isCommentEnabled) {
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

                                                commentScreenModel.addReply(
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
                                            }else {
                                                commentScreenModel.addComment(
                                                    postId, commentText,
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
                        }
                    }
                }
            }
        }
    }

    if (showPostLikeSheet) {
        LaunchedEffect(postId) {
            likesScreenModel.loadInitialData(postId)
        }
        PostLikesBottomSheet(
            onDismiss = { showPostLikeSheet = false },
            onUserClick = { selectedUserId ->
                showPostLikeSheet = false
                onDismiss()
                if (likesScreenModel.isMe(selectedUserId)) {
                    tabNavigator.current = ProfileTab
                    navigator.popUntilRoot()
                } else {
                    navigator.push(ProfileScreen(userId = selectedUserId))
                }
            },
            uiState = likeUiState,
            onActionClick = { selectedUserId, status ->
                likesScreenModel.handleFollowAction(selectedUserId, status)
            },
            onLoadNextPage = {
                likesScreenModel.loadNextPage(postId)
            },
            onRetry = {
                likesScreenModel.loadInitialData(postId)
            }
        )
    }

    if (showDeleteDialog) {
        TastyConfirmDialog(
            config = DialogConfig(
                title = stringResource(Res.string.delete_post_title),
                message = stringResource(Res.string.delete_post_message),
                confirmText = stringResource(Res.string.profile_action_reject),
                isDestructive = true,
                onConfirm = {
                    screenModel.deletePost(postId)
                }
            ),
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (uiState.isActionLoadingDelete) {
        LoadingOverlay(message = stringResource(Res.string.deleting_post))
    }
    if (uiState.isActionLoadingPin) {
        LoadingOverlay(message = null)
    }
}