package org.beem.tastymap.ui.profile.subscribers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import coil3.compose.AsyncImage
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.subscribers.SubscribeResponse
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.ui.bottomnav.ProfileTab
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.components.TastyPullToRefreshBox
import org.beem.tastymap.ui.components.responsiveContentWidth
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.common_search_placeholder
import tastymap.composeapp.generated.resources.profile_connections_title
import tastymap.composeapp.generated.resources.profile_empty_list
import tastymap.composeapp.generated.resources.profile_follow_back
import tastymap.composeapp.generated.resources.profile_metric_following
import tastymap.composeapp.generated.resources.profile_metric_subscribers
import tastymap.composeapp.generated.resources.profile_no_results
import tastymap.composeapp.generated.resources.profile_pending
import tastymap.composeapp.generated.resources.profile_subscribe
import tastymap.composeapp.generated.resources.profile_subscribed
import tastymap.composeapp.generated.resources.settings_back_cd
class SubscribersListScreen(
    private val userId: Long,
    private val initialTab: SubscriberListType = SubscriberListType.SUBSCRIBERS
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SubscribersListScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val customColors = LocalCustomColors.current
        val tabNavigator = LocalTabNavigator.current

        var selectedTab by remember { mutableStateOf(initialTab) }
        var searchQuery by remember { mutableStateOf("") }
        val listState = rememberLazyListState()

        LaunchedEffect(userId, selectedTab) {
            screenModel.loadInitialData(userId, selectedTab)
        }

        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { message ->
                ToastManager.show(message)
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
                screenModel.loadNextPage(userId)
            }
        }

        val filteredList = remember(uiState.items, searchQuery) {
            if (searchQuery.isBlank()) {
                uiState.items
            } else {
                uiState.items.filter {
                    it.username.contains(searchQuery, ignoreCase = true)
                }
            }
        }

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(Res.string.settings_back_cd),
                                    tint = customColors.textPrimary
                                )
                            }
                        },
                        title = {
                            Text(
                                text = stringResource(Res.string.profile_connections_title),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = customColors.textPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = customColors.background
                        )
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTab.ordinal,
                            containerColor = customColors.background,
                            contentColor = customColors.navy,
                            divider = {},
                            modifier = Modifier.responsiveContentWidth()
                        ) {
                            Tab(
                                selected = selectedTab == SubscriberListType.SUBSCRIBERS,
                                onClick = { selectedTab = SubscriberListType.SUBSCRIBERS },
                                text = {
                                    Text(
                                        text = stringResource(Res.string.profile_metric_subscribers),
                                        color = if (selectedTab == SubscriberListType.SUBSCRIBERS) customColors.navy else customColors.textSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            )
                            Tab(
                                selected = selectedTab == SubscriberListType.SUBSCRIBES,
                                onClick = { selectedTab = SubscriberListType.SUBSCRIBES },
                                text = {
                                    Text(
                                        text = stringResource(Res.string.profile_metric_following),
                                        color = if (selectedTab == SubscriberListType.SUBSCRIBES) customColors.navy else customColors.textSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            val boxShape = RoundedCornerShape(16.dp)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(customColors.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        modifier = Modifier.responsiveContentWidth()
                    )
                }

                TastyPullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { screenModel.refresh(userId) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .responsiveContentWidth()
                                .fillMaxHeight()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = boxShape,
                                    spotColor = Color.Black.copy(alpha = 0.35f),
                                    ambientColor = Color.Black.copy(alpha = 0.20f)
                                )
                                .background(
                                    color = customColors.background,
                                    shape = boxShape
                                )
                                .clip(boxShape)
                        ) {
                            val isError = !uiState.errorMessage.isNullOrBlank()
                            val isEmpty = uiState.items.isEmpty()

                            when {
                                // 1. İlk yükleme durumu
                                uiState.isLoading && isEmpty && !isError -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = customColors.gourmetOrange)
                                    }
                                }

                                // 2. İnternet/Ağ Hatası durumu
                                isError && isEmpty -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        FilledTonalButton(
                                            onClick = { screenModel.loadInitialData(userId, selectedTab) },
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

                                // 3. Veri hatasız çekildi ancak içerik gerçekten boş
                                filteredList.isEmpty() -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (searchQuery.isNotBlank()) {
                                                stringResource(Res.string.profile_no_results)
                                            } else {
                                                stringResource(Res.string.profile_empty_list)
                                            },
                                            style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                                        )
                                    }
                                }

                                // 4. Veri başarıyla yüklendiğinde gösterilecek liste
                                else -> {
                                    LazyColumn(
                                        state = listState,
                                        modifier = Modifier
                                            .responsiveContentWidth()
                                            .fillMaxHeight(),
                                        contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp)
                                    ) {
                                        items(
                                            items = filteredList,
                                            key = { it.id }
                                        ) { user ->
                                            SubscriberUserItem(
                                                user = user,
                                                onUserClick = {
                                                    if (screenModel.isMe(user.id)) {
                                                        tabNavigator.current = ProfileTab
                                                    } else {
                                                        navigator.push(ProfileScreen(userId = user.id))
                                                    }
                                                },
                                                onActionClick = {
                                                    screenModel.handleFollowAction(
                                                        targetUserId = user.id,
                                                        currentStatus = user.relationStatus ?: RelationStatus.NOT_FOLLOWING,
                                                    )
                                                }
                                            )
                                        }

                                        if (uiState.isLoadingMore) {
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
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val inputShape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                color = customColors.textPrimary
            ),
            placeholder = {
                Text(
                    text = stringResource(Res.string.common_search_placeholder),
                    style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = customColors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = customColors.textSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = inputShape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = customColors.surfaceVariant,
                unfocusedContainerColor = customColors.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = customColors.gourmetOrange,
                focusedTextColor = customColors.textPrimary,
                unfocusedTextColor = customColors.textPrimary
            ),
            modifier = Modifier
                .responsiveContentWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = inputShape,
                    spotColor = Color.Black.copy(alpha = 0.35f),
                    ambientColor = Color.Black.copy(alpha = 0.20f)
                )
                .height(50.dp)
        )
    }
}

@Composable
private fun SubscriberUserItem(
    user: SubscribeResponse,
    onUserClick: () -> Unit,
    onActionClick: () -> Unit = {}
) {
    val customColors = LocalCustomColors.current

    val actionStyle: ActionStyle? = when (user.relationStatus) {
        RelationStatus.FOLLOWING -> ActionStyle(
            text = stringResource(Res.string.profile_subscribed),
            backColor = Color.Transparent,
            textColor = customColors.textPrimary,
            strokeColor = customColors.textSecondary.copy(alpha = 0.4f),
            isPrimary = false
        )
        RelationStatus.PENDING -> ActionStyle(
            text = stringResource(Res.string.profile_pending),
            backColor = Color.Transparent,
            textColor = customColors.textSecondary,
            strokeColor = customColors.textSecondary.copy(alpha = 0.3f),
            isPrimary = false
        )
        RelationStatus.FOLLOW_BACK -> ActionStyle(
            text = stringResource(Res.string.profile_follow_back),
            backColor = customColors.gourmetOrange,
            textColor = Color.White,
            strokeColor = Color.Transparent,
            isPrimary = true
        )
        RelationStatus.NOT_FOLLOWING -> ActionStyle(
            text = stringResource(Res.string.profile_subscribe),
            backColor = customColors.gourmetOrange,
            textColor = Color.White,
            strokeColor = Color.Transparent,
            isPrimary = true
        )
        null -> ActionStyle(
            text = stringResource(Res.string.profile_subscribe),
            backColor = customColors.gourmetOrange,
            textColor = Color.White,
            strokeColor = Color.Transparent,
            isPrimary = true
        )
        RelationStatus.SELF -> null
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .responsiveContentWidth()
                .clickable { onUserClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(customColors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!user.profile.isNullOrBlank()) {
                    AsyncImage(
                        model = user.profile,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = customColors.placeHolderIcon,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = customColors.textPrimary
                    )
                )
            }

            actionStyle?.let { style ->
                TastyButton(
                    text = style.text,
                    onClick = onActionClick,
                    modifier = Modifier.width(130.dp),
                    isPrimary = style.isPrimary,
                    backcolor = style.backColor,
                    textcolor = style.textColor,
                    strokecolor = style.strokeColor
                )
            }
        }
    }
}

private data class ActionStyle(
    val text: String,
    val backColor: Color,
    val textColor: Color,
    val strokeColor: Color,
    val isPrimary: Boolean
)