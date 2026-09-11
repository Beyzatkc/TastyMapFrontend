package org.beem.tastymap.ui.profile.myprofile.settings.blockedusers

import TastyButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.block.BlockResponse
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.common_search_placeholder
import tastymap.composeapp.generated.resources.profile_action_unblock
import tastymap.composeapp.generated.resources.profile_empty_list
import tastymap.composeapp.generated.resources.profile_no_results
import tastymap.composeapp.generated.resources.settings_back_cd
import tastymap.composeapp.generated.resources.settings_blocked_users

class BlockedUsersScreen() : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<BlockedScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val customColors = LocalCustomColors.current

        var searchQuery by remember { mutableStateOf("") }
        val pullToRefreshState = rememberPullToRefreshState()
        val listState = rememberLazyListState()

        LaunchedEffect(Unit) {
            screenModel.loadInitialData()
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
                totalItems > 0 && lastVisibleItem >= totalItems - 2
            }
        }

        LaunchedEffect(shouldLoadMore.value) {
            if (shouldLoadMore.value) {
                screenModel.loadNextPage()
            }
        }

        val filteredList = remember(uiState.items, searchQuery) {
            if (searchQuery.isBlank()) {
                uiState.items
            } else {
                uiState.items.filter {
                    it.username?.contains(searchQuery, ignoreCase = true) ?: false
                }
            }
        }

        Scaffold(
            topBar = {
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
                            text = stringResource(Res.string.settings_blocked_users),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = customColors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = customColors.background
                    )
                )
            }
        ) { innerPadding ->
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
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        modifier = Modifier.widthIn(max = 1500.dp)
                    )
                }

                PullToRefreshBox(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { screenModel.refresh() },
                    modifier = Modifier.fillMaxSize(),
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
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
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

                            isError && isEmpty -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    FilledTonalButton(
                                        onClick = { screenModel.loadInitialData() },
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

                            else -> {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier
                                        .widthIn(max = 1500.dp)
                                        .fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    items(
                                        items = filteredList,
                                        key = { it.userId }
                                    ) { user ->
                                        BlockedUserItem(
                                            user = user,
                                            onUserClick = {
                                                navigator.push(ProfileScreen(userId = user.userId))
                                            },
                                            onUnblockClick = {
                                                screenModel.unblockUser(targetUserId = user.userId)
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

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current

    TextField(
        value = query,
        onValueChange = onQueryChange,
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
                tint = customColors.textSecondary
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
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = customColors.surfaceVariant,
            unfocusedContainerColor = customColors.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun BlockedUserItem(
    user: BlockResponse,
    onUserClick: () -> Unit,
    onUnblockClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    val username = user.username ?: "Bilinmeyen Kullanıcı"
    val profileImage =user.profilephoto

    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            if (!profileImage.isNullOrBlank()) {
                AsyncImage(
                    model = profileImage,
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
                text = username,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = customColors.textPrimary
                )
            )
        }

        TastyButton(
            text = stringResource(Res.string.profile_action_unblock),
            onClick = onUnblockClick,
            modifier = Modifier.width(130.dp),
            isPrimary = false,
            backcolor = Color.Transparent,
            textcolor = customColors.textPrimary,
            strokecolor = customColors.textSecondary.copy(alpha = 0.4f)
        )
    }
}