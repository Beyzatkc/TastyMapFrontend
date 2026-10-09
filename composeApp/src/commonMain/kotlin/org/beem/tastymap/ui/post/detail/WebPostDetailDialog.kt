package org.beem.tastymap.ui.post.detail

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import org.beem.tastymap.core.util.formatToRelativeDateTime
import org.beem.tastymap.ui.bottomnav.ProfileTab
import org.beem.tastymap.ui.post.postlike.PostLikesBottomSheet
import org.beem.tastymap.ui.post.postlike.PostLikesScreenModel
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.components.TastyConfirmDialog
import org.beem.tastymap.ui.components.DialogConfig
import org.beem.tastymap.ui.components.LoadingOverlay
import org.beem.tastymap.ui.post.mypost.updatepost.EditPostScreen
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
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
    onDismiss: () -> Unit
) {
    val uiState by screenModel.uiState.collectAsState()
    val likeUiState by likesScreenModel.uiState.collectAsState()
    val customColors = LocalCustomColors.current
    val tabNavigator = LocalTabNavigator.current
    val navigator = LocalNavigator.currentOrThrow

    var showPostLikeSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = postId) {
        screenModel.loadPostDetail(postId)
        screenModel.fetchRemotePost(postId)
    }

    LaunchedEffect(uiState.isDeletedSuccessfully) {
        if (uiState.isDeletedSuccessfully) {
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
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
            if (uiState.isLoading && uiState.post == null) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(color = customColors.gourmetOrange)
                }
            } else {
                uiState.post?.let { post ->
                    Row(modifier = Modifier.fillMaxSize()) {

                        // SOL TARAF: MEDYA
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

                        // SAĞ TARAF: DETAYLAR VE MENÜ
                        Column(
                            modifier = Modifier
                                .width(380.dp)
                                .fillMaxHeight()
                                .background(customColors.background),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f, fill = false)
                            ) {
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

                                HorizontalDivider(
                                    color = customColors.surfaceVariant.copy(alpha = 0.5f)
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    if (!post.placeName.isNullOrBlank()) {
                                        PlaceInfoCard(post = post)
                                    }
                                    PostCaptionSection(post = post)
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(customColors.background)
                            ) {
                                HorizontalDivider(
                                    color = customColors.surfaceVariant.copy(alpha = 0.5f)
                                )

                                PostActionBar(
                                    post = post,
                                    onLikeClick = { screenModel.toggleLike(postId) },
                                    onLikeCountClick = { showPostLikeSheet = true },
                                    onCommentClick = { }
                                )

                                Text(
                                    text = formatToRelativeDateTime(post.createdAt),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = customColors.textSecondary.copy(
                                            alpha = 0.6f
                                        )
                                    ),
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 4.dp
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))
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