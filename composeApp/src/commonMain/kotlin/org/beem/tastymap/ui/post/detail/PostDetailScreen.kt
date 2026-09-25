package org.beem.tastymap.ui.post.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.core.util.formatToRelativeDateTime
import org.beem.tastymap.data.model.post.PostResponse
import org.beem.tastymap.ui.components.DialogConfig
import org.beem.tastymap.ui.components.LoadingOverlay
import org.beem.tastymap.ui.components.TastyConfirmDialog
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.be_first_to_comment
import tastymap.composeapp.generated.resources.delete_post
import tastymap.composeapp.generated.resources.delete_post_message
import tastymap.composeapp.generated.resources.delete_post_title
import tastymap.composeapp.generated.resources.deleting_post
import tastymap.composeapp.generated.resources.options
import tastymap.composeapp.generated.resources.pin_post
import tastymap.composeapp.generated.resources.post
import tastymap.composeapp.generated.resources.profile_action_reject
import tastymap.composeapp.generated.resources.profile_back_cd
import tastymap.composeapp.generated.resources.profile_default_name
import tastymap.composeapp.generated.resources.unpin_post
import tastymap.composeapp.generated.resources.view_all_comments

data class PostDetailScreen(val postId: Long) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<PostDetailScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        val pullToRefreshState = rememberPullToRefreshState()
        val navigator = LocalNavigator.currentOrThrow
        val customColors = LocalCustomColors.current

        var showMenu by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }

        LaunchedEffect(postId) {
            screenModel.loadPostDetail(postId)
            screenModel.fetchRemotePost(postId)
        }

        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { message ->
                ToastManager.show(message)
                screenModel.clearError()
            }
        }

        LaunchedEffect(uiState.isDeletedSuccessfully) {
            if (uiState.isDeletedSuccessfully) {
                navigator.pop()
            }
        }

        val isInitialLoading = uiState.isLoading && uiState.post == null

        Crossfade(
            targetState = isInitialLoading,
            label = "PostDetailFullScreenLoading"
        ) { loading ->
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(customColors.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = customColors.gourmetOrange)
                }
            } else {
                Scaffold(
                    containerColor = customColors.background,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(Res.string.post),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = customColors.textPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { navigator.pop() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(Res.string.profile_back_cd),
                                        tint = customColors.textPrimary,
                                    )
                                }
                            },
                            actions = {
                                if (uiState.isOwnPost) {
                                    Box {
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
                                                        if (uiState.post?.isPinned == true)
                                                            stringResource(Res.string.unpin_post)
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
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = customColors.background
                            ),
                        )
                    }
                ) { innerPadding ->
                    PullToRefreshBox(
                        state = pullToRefreshState,
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = {
                            screenModel.refreshPost(postId)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(customColors.placeHolderBack),
                        indicator = {
                            PullToRefreshDefaults.Indicator(
                                state = pullToRefreshState,
                                isRefreshing = uiState.isRefreshing,
                                modifier = Modifier.align(Alignment.TopCenter),
                                containerColor = customColors.placeHolderBack,
                                color = customColors.placeHolderIcon
                            )
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (uiState.post != null) {
                                uiState.post?.let { post ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(customColors.background)
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        // 1. KULLANICI BİLGİSİ HEADER
                                        PostHeader(
                                            post = post,
                                            onUserClick = {
                                                // Kullanıcı profiline gitme opsiyonu
                                            }
                                        )

                                        // 2. MEDYA PAGER
                                        PostMediaPager(
                                            photoUrls = post.photoUrls,
                                            onDoubleTapLike = {
                                                if (!post.isLiked) {
                                                    screenModel.toggleLike(postId)
                                                }
                                            }
                                        )

                                        // 3. ETKİLEŞİM BUTONLARI
                                        PostActionBar(
                                            post = post,
                                            onLikeClick = { screenModel.toggleLike(postId) },
                                            onCommentClick = {
                                                // Yorumlar ekranına git
                                            }
                                        )

                                        // 4. MEKAN VE DERECELENDİRME KARTI
                                        if (!post.placeName.isNullOrBlank()) {
                                            PlaceInfoCard(post = post)
                                        }

                                        // 5. AÇIKLAMA METNİ
                                        PostCaptionSection(post = post)

                                        // 6. YORUMLARI GÖR BUTONU
                                        if (post.isCommentEnabled) {
                                            Text(
                                                text = if (post.commentCount > 0) {
                                                    stringResource(
                                                        Res.string.view_all_comments,
                                                        post.commentCount
                                                    )
                                                } else {
                                                    stringResource(Res.string.be_first_to_comment)
                                                },
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = customColors.textSecondary
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        // navigator.push(CommentScreen(post.postId))
                                                    }
                                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                            )
                                        }

                                        // 7. TARİH
                                        Text(
                                            text = formatToRelativeDateTime(post.createdAt),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = customColors.textSecondary.copy(
                                                    alpha = 0.6f
                                                )
                                            ),
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(24.dp))
                                    }
                                }
                            } else if (!uiState.isLoading && uiState.errorMessage!=null && uiState.post != null) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
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
                                                        tint = customColors.textPrimary
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = stringResource(Res.string.active_devices_retry),
                                                        style = MaterialTheme.typography.titleSmall.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = customColors.textPrimary
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (uiState.isActionLoadingDelete) {
                                LoadingOverlay(
                                    message = stringResource(Res.string.deleting_post)
                                )
                            }
                            if (uiState.isActionLoadingPin) {
                                LoadingOverlay(message = null)
                            }
                        }
                    }
                }
            }
        }

        // SİLME ONAY DİALOGU
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
    }
}

// --- SUB-COMPONENTS ---

@Composable
private fun PostHeader(
    post: PostResponse,
    onUserClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onUserClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(1.5.dp, customColors.gourmetOrange, CircleShape)
                .background(customColors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (!post.profilePhotoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = post.profilePhotoUrl,
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
            Text(
                text = post.username ?: stringResource(Res.string.profile_default_name),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = customColors.textPrimary
                )
            )
            if (!post.placeName.isNullOrBlank() || !post.city.isNullOrBlank()) {
                val locationText = listOfNotNull(post.placeName, post.city).joinToString(", ")
                Text(
                    text = locationText,
                    style = MaterialTheme.typography.bodySmall.copy(color = customColors.textSecondary),
                    maxLines = 1
                )
            }
        }

        if (post.isPinned) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun PostMediaPager(
    photoUrls: List<String>,
    onDoubleTapLike: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val pagerState = rememberPagerState(pageCount = { photoUrls.size })
    var showBigHeart by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (showBigHeart) 1.2f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "HeartScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(customColors.surfaceVariant)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        onDoubleTapLike()
                        coroutineScope.launch {
                            showBigHeart = true
                            delay(800)
                            showBigHeart = false
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (photoUrls.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = photoUrls[page],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            if (photoUrls.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(photoUrls.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 7.dp else 5.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index) customColors.gourmetOrange
                                    else Color.White.copy(alpha = 0.6f)
                                )
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showBigHeart,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size((100 * scale).dp)
            )
        }
    }
}

@Composable
private fun PostActionBar(
    post: PostResponse,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = if (post.isLiked) customColors.red else customColors.textPrimary,
                modifier = Modifier.size(26.dp)
            )
        }

        Text(
            text = "${post.likeCount}",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = customColors.textPrimary
            )
        )

        if (post.isCommentEnabled) {
            IconButton(onClick = onCommentClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Comment,
                    contentDescription = null,
                    tint = customColors.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = post.commentCount.toString(),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = customColors.textPrimary
                )
            )
        }
    }
}

@Composable
private fun PlaceInfoCard(post: PostResponse) {
    val customColors = LocalCustomColors.current

    Surface(
        color = customColors.navy.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = customColors.gourmetOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = post.placeName ?: "",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = customColors.textPrimary
                        )
                    )
                    if (!post.categories.isNullOrBlank()) {
                        Text(
                            text = post.categories,
                            style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary)
                        )
                    }
                }
            }

            if (post.averagePoint > 0) {
                Surface(
                    color = customColors.navy,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.averagePoint.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PostCaptionSection(post: PostResponse) {
    val customColors = LocalCustomColors.current

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        if (!post.explanation.isNullOrBlank()) {
            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = customColors.textPrimary
                    )
                ) {
                    append(post.username ?: "")
                    append("  ")
                }
                withStyle(style = SpanStyle(color = customColors.textPrimary)) {
                    append(post.explanation)
                }
            }

            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}