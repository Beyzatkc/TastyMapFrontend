package org.beem.tastymap.ui.profile.otherprofile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.post.PostGridResponse
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.ui.animations.shimmerEffect
import org.beem.tastymap.ui.components.DialogConfig
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.components.TastyConfirmDialog
import org.beem.tastymap.ui.post.detail.PostDetailScreen
import org.beem.tastymap.ui.post.otherpost.PostScreenModel
import org.beem.tastymap.ui.profile.subscribers.SubscriberListType
import org.beem.tastymap.ui.profile.subscribers.SubscribersListScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.dialog_block_message
import tastymap.composeapp.generated.resources.dialog_block_title
import tastymap.composeapp.generated.resources.dialog_remove_follower_message
import tastymap.composeapp.generated.resources.dialog_remove_follower_title
import tastymap.composeapp.generated.resources.dialog_unblock_message
import tastymap.composeapp.generated.resources.dialog_unblock_title
import tastymap.composeapp.generated.resources.dialog_unfollow_message
import tastymap.composeapp.generated.resources.dialog_unfollow_title
import tastymap.composeapp.generated.resources.my_profile_posts_empty
import tastymap.composeapp.generated.resources.profile_action_accept
import tastymap.composeapp.generated.resources.profile_action_block
import tastymap.composeapp.generated.resources.profile_action_reject
import tastymap.composeapp.generated.resources.profile_action_remove_follower
import tastymap.composeapp.generated.resources.profile_action_unblock
import tastymap.composeapp.generated.resources.profile_action_unfollow
import tastymap.composeapp.generated.resources.profile_back_cd
import tastymap.composeapp.generated.resources.profile_blocked_message
import tastymap.composeapp.generated.resources.profile_default_bio
import tastymap.composeapp.generated.resources.profile_default_name
import tastymap.composeapp.generated.resources.profile_default_photo_cd
import tastymap.composeapp.generated.resources.profile_follow_back
import tastymap.composeapp.generated.resources.profile_incoming_request_message
import tastymap.composeapp.generated.resources.profile_map_empty
import tastymap.composeapp.generated.resources.profile_metric_following
import tastymap.composeapp.generated.resources.profile_metric_posts
import tastymap.composeapp.generated.resources.profile_metric_subscribers
import tastymap.composeapp.generated.resources.profile_pending
import tastymap.composeapp.generated.resources.profile_photo_cd
import tastymap.composeapp.generated.resources.profile_posts_empty
import tastymap.composeapp.generated.resources.profile_private_account_message
import tastymap.composeapp.generated.resources.profile_subscribe
import tastymap.composeapp.generated.resources.profile_subscribed
import tastymap.composeapp.generated.resources.profile_tab_posts
import tastymap.composeapp.generated.resources.profile_tab_taste_map
import tastymap.composeapp.generated.resources.profile_user_unavailable

class ProfileScreen(private val userId: Long) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ProfileScreenModel> { parametersOf(userId) }
        val postScreenModel = koinScreenModel<PostScreenModel>()
        val state by screenModel.profileState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        var isPhotoZoomed by remember { mutableStateOf(false) }

        val postState by postScreenModel.uiState.collectAsState()
        val gridState = rememberLazyGridState()

        var showBottomSheet by remember { mutableStateOf(false) }
        var activeDialog by remember { mutableStateOf<DialogConfig?>(null) }

        val pullToRefreshState = rememberPullToRefreshState()
        val customColors = LocalCustomColors.current
        var selectedTab by remember { mutableIntStateOf(0) }

        val blockTitle = stringResource(Res.string.dialog_block_title,)
        val blockMessage = stringResource(Res.string.dialog_block_message,state.profile?.username ?: "")
        val blockConfirm = stringResource(Res.string.profile_action_block)

        val unblockTitle = stringResource(Res.string.dialog_unblock_title)
        val unblockMessage = stringResource(Res.string.dialog_unblock_message, state.profile?.username ?: "")
        val unblockConfirm = stringResource(Res.string.profile_action_unblock)

        val unfollowTitle = stringResource(Res.string.dialog_unfollow_title)
        val unfollowMessage = stringResource(Res.string.dialog_unfollow_message, state.profile?.username ?: "")
        val unfollowConfirm = stringResource(Res.string.profile_action_unfollow)

        val removeFollowerTitle = stringResource(Res.string.dialog_remove_follower_title)
        val removeFollowerMessage = stringResource(Res.string.dialog_remove_follower_message, state.profile?.username ?: "")
        val removeFollowerConfirm = stringResource(Res.string.profile_action_remove_follower)

        LaunchedEffect(state.errorMessage) {
            state.errorMessage?.let { message ->
                ToastManager.show(message)
                screenModel.clearError()
            }
        }
        LaunchedEffect(Unit){
            postScreenModel.loadInitialData(userId)
        }

        val shouldLoadMore = remember {
            derivedStateOf {
                val totalItemsCount = gridState.layoutInfo.totalItemsCount
                val lastVisibleItemIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleItemIndex >= totalItemsCount - 3
            }
        }

        LaunchedEffect(shouldLoadMore.value) {
            if (shouldLoadMore.value && selectedTab == 0) {
                postScreenModel.loadNextPage(userId)
            }
        }

        val isInitialLoading = state.isLoading && state.profile == null

        Crossfade(
            targetState = isInitialLoading,
            label = "ProfileFullScreenLoading"
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
                    topBar = {
                        TopAppBar(
                            navigationIcon = {
                                IconButton(onClick = { navigator.pop() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(Res.string.profile_back_cd),
                                        tint = Color.White
                                    )
                                }
                            },
                            title = {
                                Text(
                                    text = "@${state.profile?.username ?: ""}",
                                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                                )
                            },
                            actions = {
                                IconButton(onClick = {
                                    showBottomSheet = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Seçenekler",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = customColors.darkHeaderColor
                            )
                        )
                    }
                ) { innerPadding ->
                    PullToRefreshBox(
                        state = pullToRefreshState,
                        isRefreshing = state.isRefreshing,
                        onRefresh = { screenModel.refreshProfile() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(customColors.placeHolderBack),
                        indicator = {
                            PullToRefreshDefaults.Indicator(
                                state = pullToRefreshState,
                                isRefreshing = state.isRefreshing,
                                modifier = Modifier.align(Alignment.TopCenter),
                                containerColor = customColors.placeHolderBack,
                                color = customColors.placeHolderIcon
                            )
                        }
                    ) {
                        val profile = state.profile

                        if (profile == null && !state.isLoading && state.errorMessage !=null && state.isRefreshing) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                FilledTonalButton(
                                    onClick = { screenModel.refreshProfile() },
                                    enabled = !state.isLoading,
                                    modifier = Modifier.height(48.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = customColors.surfaceVariant,
                                        contentColor = customColors.textPrimary
                                    )
                                ) {
                                    AnimatedContent(
                                        targetState = state.isLoading,
                                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                                        label = "ButtonLoadingTransition"
                                    ) { isLoading ->
                                        if (isLoading) {
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
                        } else {
                            val isBlocked = profile?.blockedByMe == true || profile?.blockedMe == true
                            val isPrivateAndNotFollowing = profile?.privateProfile == true && profile.relationStatus != RelationStatus.FOLLOWING && profile.relationStatus != RelationStatus.SELF

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                state = gridState,
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                // 1. PROFİL HEADER BİLGİLERİ
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Surface(
                                            color = customColors.darkHeaderColor,
                                            shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .widthIn(max = 600.dp)
                                                        .padding(horizontal = 20.dp, vertical = 20.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    // Profil Fotoğrafı
                                                    Box(contentAlignment = Alignment.BottomEnd) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(92.dp)
                                                                .clip(CircleShape)
                                                                .border(3.dp, customColors.gourmetOrange, CircleShape)
                                                                .background(customColors.placeHolderBack)
                                                                .pointerInput(Unit) {
                                                                    detectTapGestures(onLongPress = { isPhotoZoomed = true })
                                                                },
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            if (!profile?.profilePhoto.isNullOrBlank() && !isBlocked) {
                                                                AsyncImage(
                                                                    model = profile.profilePhoto,
                                                                    contentDescription = stringResource(Res.string.profile_photo_cd),
                                                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                                    contentScale = ContentScale.Crop
                                                                )
                                                            } else {
                                                                Icon(
                                                                    imageVector = Icons.Default.Person,
                                                                    contentDescription = stringResource(Res.string.profile_default_photo_cd),
                                                                    tint = customColors.placeHolderIcon,
                                                                    modifier = Modifier.size(48.dp)
                                                                )
                                                            }
                                                        }

                                                        profile?.role?.let { role ->
                                                            Surface(
                                                                color = customColors.gourmetOrange,
                                                                shape = RoundedCornerShape(6.dp),
                                                                modifier = Modifier.offset(y = 4.dp)
                                                            ) {
                                                                Text(
                                                                    text = role,
                                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                                        color = Color.White,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    Text(
                                                        text = profile?.name?.trim().takeIf { !it.isNullOrBlank() }
                                                            ?: stringResource(Res.string.profile_default_name),
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            fontSize = 18.sp,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )

                                                    Spacer(modifier = Modifier.height(4.dp))

                                                    Text(
                                                        text = profile?.biography ?: stringResource(Res.string.profile_default_bio),
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            color = Color.White.copy(alpha = 0.8f),
                                                            textAlign = TextAlign.Center
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 16.dp)
                                                    )

                                                    Spacer(modifier = Modifier.height(20.dp))

                                                    if (profile?.hasPendingIncomingRequest == true) {
                                                        IncomingRequestCard(
                                                            username = profile.username,
                                                            isLoading = state.isActionLoading,
                                                            onAcceptClick = { screenModel.acceptRequest(userId) },
                                                            onRejectClick = { screenModel.rejectRequest(userId) }
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                    }

                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Box(modifier = Modifier.weight(1f)) {
                                                            if (!isBlocked) {
                                                                ProfileActionButton(
                                                                    relationStatus = profile?.relationStatus ?: RelationStatus.NOT_FOLLOWING,
                                                                    isLoading = state.isActionLoading,
                                                                    onActionClick = { status ->
                                                                        if (status == RelationStatus.FOLLOWING) {
                                                                            activeDialog = DialogConfig(
                                                                                title = unfollowTitle,
                                                                                message = unfollowMessage,
                                                                                confirmText = unfollowConfirm,
                                                                                isDestructive = false,
                                                                                onConfirm = { screenModel.handleFollowAction(userId, status) }
                                                                            )
                                                                        } else {
                                                                            screenModel.handleFollowAction(userId, status)
                                                                        }
                                                                    }
                                                                )
                                                            } else {
                                                                BlockActionButton(
                                                                    blockedByMe = profile.blockedByMe ?: false,
                                                                    blockedMe = profile.blockedMe ?: false,
                                                                    isLoading = state.isActionLoading,
                                                                    onActionClick = {
                                                                        val isBlockedByMe = profile.blockedByMe == true
                                                                        activeDialog = DialogConfig(
                                                                            title = if (isBlockedByMe) unblockTitle else blockTitle,
                                                                            message = if (isBlockedByMe) unblockMessage else blockMessage,
                                                                            confirmText = if (isBlockedByMe) unblockConfirm else blockConfirm,
                                                                            isDestructive = !isBlockedByMe,
                                                                            onConfirm = { screenModel.toggleBlockStatus(userId) }
                                                                        )
                                                                    }
                                                                )
                                                            }
                                                        }

                                                        if (profile?.relationStatus != RelationStatus.SELF) {
                                                            IconButton(
                                                                onClick = { /* Paylaş */ },
                                                                modifier = Modifier
                                                                    .size(48.dp)
                                                                    .background(
                                                                        color = Color.White.copy(alpha = 0.15f),
                                                                        shape = RoundedCornerShape(12.dp)
                                                                    )
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Share,
                                                                    contentDescription = "Profili Paylaş",
                                                                    tint = Color.White
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }

                                // 2. METRİK KARTLAR (3 Sütunu Kaplar)
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Column {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val isClickable = profile?.relationStatus == RelationStatus.FOLLOWING || profile?.relationStatus == RelationStatus.SELF
                                            Row(
                                                modifier = Modifier.widthIn(max = 600.dp),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                MetricCard(
                                                    title = stringResource(Res.string.profile_metric_posts),
                                                    value = (profile?.postCount ?: 0).toString(),
                                                    enabled = isClickable,
                                                    onClick = {},
                                                    modifier = Modifier.weight(1f),
                                                    cardColor = customColors.surfaceVariant
                                                )
                                                MetricCard(
                                                    title = stringResource(Res.string.profile_metric_subscribers),
                                                    value = (profile?.subscriberCount ?: 0).toString(),
                                                    enabled = isClickable,
                                                    onClick = {
                                                        state.profile?.userId?.let { uId ->
                                                            navigator.push(SubscribersListScreen(userId = uId, initialTab = SubscriberListType.SUBSCRIBERS))
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    cardColor = customColors.surfaceVariant
                                                )
                                                MetricCard(
                                                    title = stringResource(Res.string.profile_metric_following),
                                                    value = (profile?.subscribedCount ?: 0).toString(),
                                                    enabled = isClickable,
                                                    onClick = {
                                                        state.profile?.userId?.let { uId ->
                                                            navigator.push(SubscribersListScreen(userId = uId, initialTab = SubscriberListType.SUBSCRIBES))
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    cardColor = customColors.surfaceVariant
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }

                                // 3. İÇERİK SEÇİMİ VE GİZLİLİK KONTROLLERİ
                                if (isBlocked) {
                                    // ENGELLENMİŞ DURUM
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = customColors.textSecondary,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = stringResource(Res.string.profile_blocked_message),
                                                style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                } else if (isPrivateAndNotFollowing) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = customColors.textSecondary,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = stringResource(Res.string.profile_private_account_message),
                                                style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                } else {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Column {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Surface(
                                                    color = customColors.surfaceVariant,
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier.widthIn(max = 600.dp)
                                                ) {
                                                    Row(modifier = Modifier.padding(4.dp)) {
                                                        TabButton(
                                                            text = stringResource(Res.string.profile_tab_posts),
                                                            icon = Icons.Default.GridOn,
                                                            isSelected = selectedTab == 0,
                                                            onClick = { selectedTab = 0 },
                                                            modifier = Modifier.weight(1f),
                                                            activeColor = customColors.navy,
                                                            accentColor = customColors.gourmetOrange
                                                        )
                                                        TabButton(
                                                            text = stringResource(Res.string.profile_tab_taste_map),
                                                            icon = Icons.Default.Map,
                                                            isSelected = selectedTab == 1,
                                                            onClick = { selectedTab = 1 },
                                                            modifier = Modifier.weight(1f),
                                                            activeColor = customColors.navy,
                                                            accentColor = customColors.gourmetOrange
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }

                                    if (selectedTab == 0) {
                                        if (postState.isLoading && postState.items.isEmpty()) {
                                            item(span = { GridItemSpan(maxLineSpan) }) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(48.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(32.dp),
                                                        color = customColors.gourmetOrange,
                                                        strokeWidth = 3.dp
                                                    )
                                                }
                                            }
                                        } else if (postState.items.isEmpty()) {
                                            item(span = { GridItemSpan(maxLineSpan) }) {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = stringResource(Res.string.my_profile_posts_empty),
                                                        style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary),
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        } else {
                                            items(
                                                items = postState.items,
                                                key = { post -> post.postId }
                                            ) { post ->
                                                PostGridItem(
                                                    post = post,
                                                    onClick = {
                                                        navigator.push(PostDetailScreen(post.postId))
                                                    }
                                                )
                                            }
                                            if (postState.isLoadingMore) {
                                                item(span = { GridItemSpan(maxLineSpan) }) {
                                                    Box(
                                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(24.dp),
                                                            color = customColors.gourmetOrange,
                                                            strokeWidth = 2.dp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = stringResource(Res.string.profile_map_empty),
                                                    style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showBottomSheet && state.profile != null) {
            val profile = state.profile!!
            ProfileOptionsBottomSheet(
                isBlockedByMe = profile.blockedByMe == true,
                isFollower = profile.isFollower,
                onDismiss = { showBottomSheet = false },
                onBlockToggleClick = {
                    showBottomSheet = false
                    val isBlockedByMe = profile.blockedByMe == true
                    activeDialog = DialogConfig(
                        title = if (isBlockedByMe) unblockTitle else blockTitle,
                        message = if (isBlockedByMe) unblockMessage else blockMessage,
                        confirmText = if (isBlockedByMe) unblockConfirm else blockConfirm,
                        isDestructive = !isBlockedByMe,
                        onConfirm = {
                            screenModel.toggleBlockStatus(userId)
                        }
                    )
                },
                onRemoveFollowerClick = {
                    showBottomSheet = false
                    activeDialog = DialogConfig(
                        title = removeFollowerTitle,
                        message = removeFollowerMessage,
                        confirmText = removeFollowerConfirm,
                        isDestructive = false,
                        onConfirm = {
                            screenModel.removeFollower(userId)
                        }
                    )
                },
                onReportClick = {
                    // Şikayet ekranına yönlendirme veya diyalog açma
                    println("Kullanıcı şikayet edilecek: $userId")
                }
            )
        }
        activeDialog?.let { config ->
            TastyConfirmDialog(
                config = config,
                onDismiss = { activeDialog = null }
            )
        }
        if (isPhotoZoomed && !state.profile?.profilePhoto.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .zIndex(10f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isPhotoZoomed = false
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = state.profile?.profilePhoto,
                    contentDescription = null,
                    modifier = Modifier
                        .size(280.dp)
                        .clip(CircleShape)
                        .border(4.dp, customColors.gourmetOrange, CircleShape)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {},
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

}

@Composable
private fun BlockActionButton(
    blockedByMe: Boolean,//ben engelleıdm
    blockedMe: Boolean,// o beni engelledı
    isLoading: Boolean,
    onActionClick: () -> Unit
){
    val customColors = LocalCustomColors.current
    if(blockedByMe){
        TastyButton(
            text = stringResource(Res.string.profile_action_unblock),//engelı kaldır
            onClick = { onActionClick() },
            modifier = Modifier.fillMaxWidth(),
            isPrimary = true,
            isLoading = isLoading,
            backcolor = customColors.red,
            textcolor = Color.White,
            strokecolor = Color.Transparent
        )
        return
    }
    if (blockedMe) {
        TastyButton(
            text = stringResource(Res.string.profile_user_unavailable),
            onClick = {  },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            isPrimary = false,
            isLoading = false,
            backcolor = customColors.surfaceVariant, // Gri/Silik arka plan
            textcolor = customColors.navy,  // Silik yazı rengi
            strokecolor = Color.Transparent
        )
        return
    }
}

@Composable
private fun ProfileActionButton(
    relationStatus: RelationStatus,
    isLoading: Boolean,
    onActionClick: (RelationStatus) -> Unit
) {
    val customColors = LocalCustomColors.current

    if (relationStatus == RelationStatus.SELF) return

    val (buttonText, backColor, textColor, strokeColor) = when (relationStatus) {
        RelationStatus.FOLLOWING -> Tuple4(
            stringResource(Res.string.profile_subscribed),
            Color.White.copy(alpha = 0.18f), // Belirgin yarısaydam beyaz dolgu
            Color.White,                     // Net beyaz yazı
            Color.White.copy(alpha = 0.7f)   // Daha belirgin ve net kenarlık
        )
        RelationStatus.PENDING -> Tuple4(
            stringResource(Res.string.profile_pending),
            Color.White.copy(alpha = 0.08f), // FOLLOWING'e göre daha hafif, tatlı bir dolgu
            Color.White.copy(alpha = 0.85f), // Bekleme hissi veren hafif mat beyaz yazı
            Color.White.copy(alpha = 0.5f)   // Belirgin, ince kenarlık
        )
        RelationStatus.FOLLOW_BACK -> Tuple4(
            stringResource(Res.string.profile_follow_back),//sende takıp et
            customColors.gourmetOrange,
            Color.White,
            Color.Transparent
        )
        RelationStatus.NOT_FOLLOWING -> Tuple4(
            stringResource(Res.string.profile_subscribe),
            customColors.gourmetOrange,
            Color.White,
            Color.Transparent
        )
        RelationStatus.SELF -> Tuple4("", Color.Transparent, Color.Transparent, Color.Transparent)
    }

    TastyButton(
        text = buttonText,
        onClick = { onActionClick(relationStatus) },
        modifier = Modifier.fillMaxWidth(),
        isPrimary = true,
        isLoading = isLoading,
        backcolor = backColor,
        textcolor = textColor,
        strokecolor = strokeColor
    )
}

private data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Composable
private fun MetricCard(
    title: String,
    value: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardColor: Color
) {
    val customColors = LocalCustomColors.current

    Surface(
        onClick = onClick,
        enabled = enabled,
        color = cardColor,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(color = customColors.textPrimary)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = customColors.textSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color,
    accentColor: Color
) {
    val customColors = LocalCustomColors.current

    Surface(
        color = if (isSelected) activeColor else Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isSelected) accentColor else customColors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = if (isSelected) MaterialTheme.typography.titleMedium.copy(color = customColors.surface)
                else MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
            )
        }
    }
}
@Composable
private fun IncomingRequestCard(
    username: String,
    isLoading: Boolean,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    Surface(
        color = customColors.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.profile_incoming_request_message, username),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = customColors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = customColors.gourmetOrange
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Onayla Butonu
                    TastyButton(
                        text = stringResource(Res.string.profile_action_accept),
                        onClick = onAcceptClick,
                        modifier = Modifier.weight(1f),
                        isPrimary = true,
                        backcolor = customColors.gourmetOrange,
                        textcolor = Color.White
                    )

                    // Sil / Reddet Butonu
                    TastyButton(
                        text = stringResource(Res.string.profile_action_reject),
                        onClick = onRejectClick,
                        modifier = Modifier.weight(1f),
                        isPrimary = false,
                        backcolor = Color.Transparent,
                        textcolor = customColors.textSecondary,
                        strokecolor = customColors.textSecondary.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PostGridItem(
    post: PostGridResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current

    Box(
        modifier = modifier
            .aspectRatio(1f) // 1:1 Kare Oran
            .background(customColors.surfaceVariant)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = post.photoUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (post.isPinned) {
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.TopEnd)
                    .size(30.dp) // Yuvarlağın toplam boyutu
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

