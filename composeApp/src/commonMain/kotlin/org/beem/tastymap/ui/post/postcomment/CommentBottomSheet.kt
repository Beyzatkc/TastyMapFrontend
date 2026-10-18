package org.beem.tastymap.ui.post.postcomment

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.beem.tastymap.core.util.formatToRelativeDateTime
import org.beem.tastymap.data.model.postcomment.CommentsResponse
import org.beem.tastymap.ui.components.CommentInputBar
import org.beem.tastymap.ui.components.DialogConfig
import org.beem.tastymap.ui.components.LoadingOverlay
import org.beem.tastymap.ui.components.MentionFormattedText
import org.beem.tastymap.ui.components.TastyConfirmDialog
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.be_first_to_comment
import tastymap.composeapp.generated.resources.comments_title
import tastymap.composeapp.generated.resources.delete_post_message
import tastymap.composeapp.generated.resources.delete_post_title
import tastymap.composeapp.generated.resources.hide_replies
import tastymap.composeapp.generated.resources.profile_action_reject
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
    val coroutineScope = rememberCoroutineScope()

    var commentToDelete by remember { mutableStateOf<Long?>(null) }
    var commentText by remember { mutableStateOf("") }
    var isSendingComment by remember { mutableStateOf(false) }
    val currentUserProfileUrl = screenModel.getCurrentUserProfileUrl()

    var replyingToCommentId by remember { mutableStateOf<Long?>(null) }
    var replyingToUsername by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(postId) {
        if(isCommentEnabled) {
            screenModel.clearState()
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
        sheetState = sheetState,
        containerColor = customColors.background,
        dragHandle = { BottomSheetDefaults.DragHandle(color = customColors.placeHolderIcon) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .navigationBarsPadding()
                .imePadding()
        ){
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
                    }

                    uiState.isLoading && isEmpty && !isError -> {
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
                                onClick = { screenModel.loadInitialData(postId) },
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
                                    checkIsOwnComment = { userId -> screenModel.isMe(userId) },
                                    onToggleReplies = {
                                        screenModel.toggleReplies(
                                            postId,
                                            comment.id
                                        )
                                    },
                                    onLoadMoreReplies = {
                                        screenModel.loadNextRepliesPage(
                                            postId,
                                            comment.id
                                        )
                                    },
                                    onReplyClick = { parentId, username ->
                                        replyingToCommentId = parentId
                                        replyingToUsername = username
                                    },
                                    onToggleLike = { id ->
                                        screenModel.toggleLike(id,postId)
                                    },
                                    onDeleteClick = { commentId ->
                                        commentToDelete = commentId
                                    },
                                    onPinClick = {
                                        // TODO: screenModel.togglePin(comment.id, postId)
                                    },
                                    onUpdateClick = {
                                        // TODO: Yorum güncelleme state'ini/ui'ını aktifleştir
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
                            }else if (isCommentEnabled && uiState.loadingMoreError){
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        IconButton(
                                            onClick = {
                                                screenModel.loadNextPage(postId)
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
                }
            }
            if (commentToDelete != null) {
                TastyConfirmDialog(
                    config = DialogConfig(
                        title = "Yorumu Sil", // stringResource(Res.string.delete_comment_title)
                        message = "Bu yorumu silmek istediğinize emin misiniz?",
                        confirmText = stringResource(Res.string.profile_action_reject),
                        isDestructive = true,
                        onConfirm = {
                            // ID'yi kullanarak ScreenModel'daki delete metodunu çağırıyoruz
                            screenModel.delete(commentId = commentToDelete!!, postId = postId)
                            commentToDelete = null // Dialogu kapat
                        }
                    ),
                    onDismiss = { commentToDelete = null } // Vazgeçilirse null yap
                )
            }
            if (uiState.isActionLoadingDelete) {
                LoadingOverlay(message = "Yorum siliniyor...") // stringResource ile de verebilirsin
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
                                contents = finalReplyText, // Buraya formatlı metni verdik
                                onSuccess = {
                                    commentText = ""
                                    replyingToCommentId = null
                                    replyingToUsername = null
                                    isSendingComment = false
                                }
                            )
                        }else {
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
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentItem(
    comment: CommentsResponse,
    uiState: CommentListUiState,
    isWeb: Boolean,
    checkIsOwnComment: (Long) -> Boolean,
    onUserClick: () -> Unit,
    onToggleReplies: () -> Unit,
    onLoadMoreReplies: () -> Unit,
    onToggleLike: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onPinClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onReplyClick: (Long, String) -> Unit
) {
    val customColors = LocalCustomColors.current
    val isRepliesExpanded = uiState.expandedComments.contains(comment.id)
    val replyState = uiState.repliesState[comment.id]

    var showMenu by remember { mutableStateOf(false) }
    val isOwnComment = checkIsOwnComment(comment.userId)

    // --- HOVER (Mouse ile üzerine gelme) KONTROLÜ ---
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
                // ... Profil Fotoğrafı (Eski kodla aynı) ...
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
                        Text(text = formatToRelativeDateTime(comment.date), style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary))

                        if (isWeb && isHovered) {
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Seçenekler", tint = customColors.textSecondary, modifier = Modifier.size(16.dp))
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
                                checkIsOwnComment = checkIsOwnComment,
                                onUserClick = { onUserClick() },
                                onReplyClick = { onReplyClick(comment.id, reply.username) },
                                onDeleteClick = { onDeleteClick(reply.id) },
                                onUpdateClick = { /* TODO: Yanıt güncelleme */ },
                                onToggleLike = { /* TODO: Yanıt beğenme */ }
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
            onDismissRequest = { showMenu = false },
            onPinClick = onPinClick,
            onUpdateClick = onUpdateClick,
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
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReplyItem(
    reply: CommentsResponse,
    isWeb: Boolean,
    checkIsOwnComment: (Long) -> Boolean,
    onUserClick: () -> Unit,
    onReplyClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onToggleLike: () -> Unit
) {
    val customColors = LocalCustomColors.current
    var showMenu by remember { mutableStateOf(false) }

    val isOwnReply = checkIsOwnComment(reply.userId)

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // 1. Modifiye ediciyi platforma göre ayarlıyoruz
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
        Row(
            verticalAlignment = Alignment.Top,
            modifier = baseModifier // HATA BURADAYDI: baseModifier olarak güncellendi!
        ) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = reply.username, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = customColors.textPrimary), modifier = Modifier.clickable { onUserClick() })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = formatToRelativeDateTime(reply.date), style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary))

                    // FARE YANITIN ÜZERİNDEYSE 3 NOKTAYI GÖSTER (Yanıt kimin olursa olsun çıkar)
                    if (isWeb && isHovered) {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Seçenekler", tint = customColors.textSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                MentionFormattedText(text = reply.contents)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.reply_action),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = customColors.textSecondary
                    ),
                    modifier = Modifier.clickable { onReplyClick() }
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { onToggleLike() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (reply.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (reply.isLiked) customColors.red else customColors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (reply.numberOfLikes > 0) {
                    Text(
                        text = reply.numberOfLikes.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary)
                    )

                }
            }
        }

        // Menü çağrıldığında kimin yorumu ise ona göre Butonları Gösterir
        CommentActionDropdownMenu(
            expanded = showMenu,
            isOwnComment = isOwnReply,
            showPinOption = false,
            onDismissRequest = { showMenu = false },
            onUpdateClick = onUpdateClick,
            onDeleteClick = {
                showMenu = false
                onDeleteClick()
            },
            onReportClick = {
                showMenu = false
                // TODO: Şikayet et
            }
        )
    }
}
@Composable
fun CommentActionDropdownMenu(
    expanded: Boolean,
    isOwnComment: Boolean,
    showPinOption: Boolean = true,
    onDismissRequest: () -> Unit,
    onPinClick: () -> Unit = {},
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        containerColor = customColors.background
    ) {
        if (isOwnComment) {
            // SADECE showPinOption TRUE İSE PİN BUTONU ÇIKAR
            if (showPinOption) {
                DropdownMenuItem(
                    text = { Text("Pinle", color = customColors.textPrimary) },
                    leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null, tint = customColors.textPrimary) },
                    onClick = {
                        onDismissRequest()
                        onPinClick()
                    }
                )
            }

            DropdownMenuItem(
                text = { Text("Güncelle", color = customColors.textPrimary) },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = customColors.textPrimary) },
                onClick = {
                    onDismissRequest()
                    onUpdateClick()
                }
            )
            DropdownMenuItem(
                text = { Text("Sil", color = customColors.red) },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = customColors.red) },
                onClick = {
                    onDismissRequest()
                    onDeleteClick()
                }
            )
        } else {
            DropdownMenuItem(
                text = { Text("Şikayet Et", color = customColors.red) },
                leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = customColors.red) },
                onClick = {
                    onDismissRequest()
                    onReportClick()
                }
            )
        }
    }
}