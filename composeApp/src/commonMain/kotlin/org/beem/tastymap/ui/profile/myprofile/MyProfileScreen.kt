package org.beem.tastymap.ui.profile.myprofile

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import coil3.compose.AsyncImage
import org.beem.tastymap.data.model.post.PostGridResponse
import org.beem.tastymap.ui.bottomnav.LocalIsWideScreen
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.components.TastyPullToRefreshBox
import org.beem.tastymap.ui.components.responsiveContentWidth
import org.beem.tastymap.ui.post.detail.PostDetailViewModel
import org.beem.tastymap.ui.post.detail.WebPostDetailDialog
import org.beem.tastymap.ui.post.mypost.MyPostViewModel
import org.beem.tastymap.ui.post.postcomment.CommentViewModel
import org.beem.tastymap.ui.post.postlike.PostLikesViewModel
import org.beem.tastymap.ui.profile.subscribers.SubscriberListType
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.connection_error
import tastymap.composeapp.generated.resources.my_profile_default_bio
import tastymap.composeapp.generated.resources.my_profile_default_name
import tastymap.composeapp.generated.resources.my_profile_default_photo_cd
import tastymap.composeapp.generated.resources.my_profile_edit_button
import tastymap.composeapp.generated.resources.my_profile_map_empty
import tastymap.composeapp.generated.resources.my_profile_metric_following
import tastymap.composeapp.generated.resources.my_profile_metric_posts
import tastymap.composeapp.generated.resources.my_profile_metric_subscribers
import tastymap.composeapp.generated.resources.my_profile_photo_cd
import tastymap.composeapp.generated.resources.my_profile_posts_empty
import tastymap.composeapp.generated.resources.my_profile_tab_map
import tastymap.composeapp.generated.resources.my_profile_tab_posts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController // YENİ: NavController
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.common.NotificationBadgeManager
import org.beem.tastymap.ui.post.PostDetailRoute
import org.beem.tastymap.ui.profile.EditProfileRoute
import org.beem.tastymap.ui.profile.NotificationRoute
import org.beem.tastymap.ui.profile.SettingsRoute
import org.beem.tastymap.ui.profile.SubscribersListRoute
import org.koin.compose.viewmodel.koinViewModel
import tastymap.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(navController: NavController) {
    val profileScreenModel = koinViewModel<MyProfileViewModel>()
    val postScreenModel = koinViewModel<MyPostViewModel>()
    val postDetailViewModel = koinViewModel<PostDetailViewModel>()
    val commentViewModel = koinViewModel<CommentViewModel>()
    val postLikesViewModel = koinViewModel<PostLikesViewModel>()

    var selectedWebPostId by remember { mutableStateOf<Long?>(null) }
    val profileState by profileScreenModel.myProfileState.collectAsState()
    val postState by postScreenModel.uiState.collectAsState()
    val badgeManager = koinInject<NotificationBadgeManager>()
    val hasUnread by badgeManager.hasUnreadBadge.collectAsState()

    var isPhotoZoomed by remember { mutableStateOf(false) }
    val customColors = LocalCustomColors.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val isWideScreen = LocalIsWideScreen.current

    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        profileScreenModel.getMyProfile()
        profileScreenModel.fetchRemoteProfile()
        postScreenModel.loadInitialData()
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
            postScreenModel.loadNextPage()
        }
    }

    LaunchedEffect(profileState.successMessageRes, profileState.errorMessage) {
        profileState.successMessageRes?.let { res ->
            ToastManager.show(getString(res))
            profileScreenModel.clearMessagesProfile()
        }
        profileState.errorMessage?.let { message ->
            ToastManager.show(message)
            profileScreenModel.clearMessagesProfile()
        }
    }

    val isInitialLoading = profileState.isLoading && profileState.profile == null

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
                        title = {
                            Text(
                                text = "@" + (profileState.profile?.username ?: ""),
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                            )
                        },
                        actions = {
                            if (!isWideScreen) {
                                IconButton(onClick = {
                                    navController.navigate(NotificationRoute)
                                }) {
                                    BadgedBox(
                                        badge = {
                                            if (hasUnread) {
                                                Badge(containerColor = Color.Red, modifier = Modifier.size(8.dp))
                                            }
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                                    }
                                }
                                IconButton(onClick = {
                                    navController.navigate(SettingsRoute)
                                }) {
                                    Icon(imageVector = Icons.Default.Menu, contentDescription = null, tint = Color.White)
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = customColors.darkHeaderColor
                        )
                    )
                }
            ) { innerPadding ->
                TastyPullToRefreshBox(
                    isRefreshing = profileState.isRefreshing || postState.isRefreshing,
                    onRefresh = {
                        profileScreenModel.refreshMyProfile()
                        postScreenModel.refresh()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(customColors.bottomBackground)
                ){
                    val profile = profileState.profile

                    if (profile == null && !profileState.isLoading && profileState.errorMessage != null && profileState.isRefreshing) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    profileScreenModel.refreshMyProfile()
                                    postScreenModel.refresh()
                                },
                                enabled = !profileState.isLoading,
                                modifier = Modifier.height(48.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = customColors.surfaceVariant,
                                    contentColor = customColors.textPrimary
                                )
                            ) {
                                AnimatedContent(
                                    targetState = profileState.isLoading,
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
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            state = gridState,
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // 1. PROFİL BAŞLIK ALANI
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        color = customColors.darkHeaderColor,
                                        shape = RoundedCornerShape(
                                            bottomStart = 28.dp,
                                            bottomEnd = 28.dp
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .responsiveContentWidth()
                                                    .padding(
                                                        horizontal = 20.dp,
                                                        vertical = 20.dp
                                                    ),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Box(contentAlignment = Alignment.BottomEnd) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(92.dp)
                                                            .clip(CircleShape)
                                                            .border(
                                                                3.dp,
                                                                customColors.gourmetOrange,
                                                                CircleShape
                                                            )
                                                            .background(customColors.placeHolderBack)
                                                            .pointerInput(Unit) {
                                                                detectTapGestures(onLongPress = {
                                                                    isPhotoZoomed = true
                                                                })
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (!profile?.profilePhoto.isNullOrBlank()) {
                                                            AsyncImage(
                                                                model = profile.profilePhoto,
                                                                contentDescription = stringResource(
                                                                    Res.string.my_profile_photo_cd
                                                                ),
                                                                modifier = Modifier.fillMaxSize()
                                                                    .clip(CircleShape),
                                                                contentScale = ContentScale.Crop
                                                            )
                                                        } else {
                                                            Icon(
                                                                imageVector = Icons.Default.Person,
                                                                contentDescription = stringResource(
                                                                    Res.string.my_profile_default_photo_cd
                                                                ),
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
                                                                modifier = Modifier.padding(
                                                                    horizontal = 8.dp,
                                                                    vertical = 2.dp
                                                                ),
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
                                                    text = profile?.name
                                                        ?: stringResource(Res.string.my_profile_default_name),
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontSize = 18.sp,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = profile?.biography
                                                        ?: stringResource(Res.string.my_profile_default_bio),
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        color = Color.White.copy(alpha = 0.8f),
                                                        textAlign = TextAlign.Center
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 16.dp)
                                                )

                                                Spacer(modifier = Modifier.height(20.dp))

                                                TastyButton(
                                                    text = stringResource(Res.string.my_profile_edit_button),
                                                    onClick = {
                                                        navController.navigate(EditProfileRoute)
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    isPrimary = true,
                                                    isLoading = profileState.isActionLoading,
                                                    backcolor = customColors.gourmetOrange,
                                                    textcolor = Color.White,
                                                    strokecolor = Color.Transparent
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column {
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            modifier = Modifier.responsiveContentWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ){
                                            MetricCard(
                                                title = stringResource(Res.string.my_profile_metric_posts),
                                                value = (profile?.postCount ?: 0).toString(),
                                                onClick = {},
                                                modifier = Modifier.weight(1f),
                                                cardColor = customColors.surfaceVariant
                                            )
                                            MetricCard(
                                                title = stringResource(Res.string.my_profile_metric_subscribers),
                                                value = (profile?.subscriberCount ?: 0).toString(),
                                                onClick = {
                                                    profile?.userId?.let { userId ->
                                                        navController.navigate(
                                                            SubscribersListRoute(
                                                                userId = userId,
                                                                initialTab = SubscriberListType.SUBSCRIBERS
                                                            )
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.weight(1f),
                                                cardColor = customColors.surfaceVariant
                                            )
                                            MetricCard(
                                                title = stringResource(Res.string.my_profile_metric_following),
                                                value = (profile?.subscribedCount ?: 0).toString(),
                                                onClick = {
                                                    profile?.userId?.let { userId ->
                                                        // YENİ: navigator.push yerine navigate
                                                        navController.navigate(
                                                            SubscribersListRoute(userId = userId, initialTab = SubscriberListType.SUBSCRIBES)
                                                        )
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

                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column {
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            color = customColors.surfaceVariant,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.responsiveContentWidth()
                                        ) {
                                            Row(modifier = Modifier.padding(4.dp)) {
                                                TabButton(
                                                    text = stringResource(Res.string.my_profile_tab_posts),
                                                    icon = Icons.Default.GridOn,
                                                    isSelected = selectedTab == 0,
                                                    onClick = { selectedTab = 0 },
                                                    modifier = Modifier.weight(1f),
                                                    activeColor = customColors.navy,
                                                    accentColor = customColors.gourmetOrange
                                                )
                                                TabButton(
                                                    text = stringResource(Res.string.my_profile_tab_map),
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

                            // 4. İÇERİK BÖLÜMÜ (SEKMEYE GÖRE DEĞİŞİR)
                            if (selectedTab == 0) {
                                if (postState.isLoading && postState.items.isEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .responsiveContentWidth()
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
                                    }
                                } else if (postState.errorMessage != null && postState.items.isEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Column(
                                            modifier = Modifier
                                                .responsiveContentWidth()
                                                .fillMaxWidth()
                                                .padding(vertical = 16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    postScreenModel.loadInitialData()
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
                                } else if (postState.items.isEmpty()) {
                                    // 2. BOŞ DURUM (Empty State)
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = stringResource(Res.string.my_profile_posts_empty),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = customColors.textSecondary
                                                ),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                } else {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(modifier = Modifier.responsiveContentWidth()) {
                                                val chunkedPosts = postState.items.chunked(3)
                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    chunkedPosts.forEach { rowItems ->
                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            for (post in rowItems) {
                                                                Box(
                                                                    modifier = Modifier.weight(1f)
                                                                ) {
                                                                    PostGridItem(
                                                                        post = post,
                                                                        onClick = {
                                                                            if (isWideScreen) {
                                                                                selectedWebPostId = post.postId
                                                                            } else {
                                                                                navController.navigate(
                                                                                    PostDetailRoute(
                                                                                        post.postId
                                                                                    )
                                                                                )
                                                                            }
                                                                        }
                                                                    )
                                                                }
                                                            }
                                                            repeat(3 - rowItems.size) {
                                                                Spacer(modifier = Modifier.weight(1f))
                                                            }
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(40.dp))
                                                }
                                            }
                                        }
                                    }

                                    // 4. SAYFALAMA / DAHA FAZLA YÜKLENİYOR INDICATOR
                                    if (postState.isLoadingMore) {
                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier.responsiveContentWidth()
                                                        .padding(16.dp),
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
                                    if (postState.loadingMoreError && !postState.isLoadingMore) {
                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .responsiveContentWidth()
                                                        .padding(vertical = 10.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    IconButton(onClick = { postScreenModel.loadNextPage() }) {
                                                        Icon(
                                                            imageVector = Icons.Default.Refresh,
                                                            contentDescription = stringResource(
                                                                Res.string.active_devices_retry
                                                            ),
                                                            tint = customColors.placeHolderIcon,
                                                            modifier = Modifier.size(28.dp)
                                                        )
                                                    }
                                                    Text(
                                                        text = stringResource(Res.string.connection_error),
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = customColors.placeHolderIcon,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier.responsiveContentWidth()
                                                .padding(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = stringResource(Res.string.my_profile_map_empty),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = customColors.textSecondary
                                                ),
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
    if (selectedWebPostId != null && isWideScreen) {
        WebPostDetailDialog(
            postId = selectedWebPostId!!,
            screenModel = postDetailViewModel,
            likesScreenModel = postLikesViewModel,
            commentViewModel = commentViewModel,
            navController = navController,
            onDismiss = { selectedWebPostId = null }
        )
    }

    if (isPhotoZoomed && !profileState.profile?.profilePhoto.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .zIndex(10f)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { isPhotoZoomed = false },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = profileState.profile?.profilePhoto,
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

@Composable
private fun MetricCard(
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardColor: Color
) {
    val customColors = LocalCustomColors.current

    Surface(
        onClick = onClick,
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
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color,
    accentColor: Color
) {
    val customColors = LocalCustomColors.current

    Surface(
        onClick = onClick,
        color = if (isSelected) activeColor else Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
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
private fun PostGridItem(
    post: PostGridResponse, // NOT: PostGridResponse modelinin projende bulunduğundan emin ol
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current

    Box(
        modifier = modifier
            .aspectRatio(1f)
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