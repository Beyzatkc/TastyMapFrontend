package org.beem.tastymap.ui.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import coil3.compose.AsyncImage
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.search.UserSearchResponse
import org.beem.tastymap.ui.bottomnav.ProfileTab
import org.beem.tastymap.ui.profile.myprofile.MyProfileScreen
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.active_devices_retry
import tastymap.composeapp.generated.resources.active_devices_retry_cd
import tastymap.composeapp.generated.resources.common_search_placeholder
import tastymap.composeapp.generated.resources.profile_empty_list
import tastymap.composeapp.generated.resources.profile_no_results
import tastymap.composeapp.generated.resources.search_recent_title
import tastymap.composeapp.generated.resources.search_remove_from_history
import tastymap.composeapp.generated.resources.settings_back_cd

class SearchScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SearchScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val customColors = LocalCustomColors.current
        val listState = rememberLazyListState()
        val tabNavigator = LocalTabNavigator.current

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

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        AnimatedVisibility(
                            visible = uiState.query.isNotEmpty(),
                            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                            exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
                        ) {
                            IconButton(
                                onClick = {
                                    screenModel.clearQuery()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(Res.string.settings_back_cd),
                                    tint = customColors.textPrimary
                                )
                            }
                        }
                    },
                    title = {
                        SearchBarInput(
                            query = uiState.query,
                            onQueryChange = { screenModel.onQueryChanged(it) },
                            onClearClick = { screenModel.clearQuery() }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = customColors.background
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(customColors.background),
                contentAlignment = Alignment.TopCenter
            ) {
                val isError = !uiState.errorMessage.isNullOrBlank()
                val currentList = if (uiState.isHistoryMode) uiState.historyResults else uiState.searchResults
                val isEmpty = currentList.isEmpty()

                when {
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
                                onClick = { screenModel.refresh() },
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

                    isEmpty && !uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.isHistoryMode) {
                                    stringResource(Res.string.profile_empty_list)
                                } else {
                                    stringResource(Res.string.profile_no_results)
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
                            contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp)
                        ) {
                            if (uiState.isHistoryMode && !isEmpty) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = customColors.textSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(Res.string.search_recent_title),
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = customColors.textSecondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            items(
                                items = currentList,
                                key = { it.id }
                            ) { user ->
                                SearchUserItem(
                                    user = user,
                                    isHistoryItem = uiState.isHistoryMode,
                                    onUserClick = {
                                        screenModel.onUserClicked(user)
                                        if (screenModel.isMe(user.id)) {
                                            tabNavigator.current = ProfileTab
                                        } else {
                                            navigator.push(ProfileScreen(userId = user.id))
                                        }
                                    },
                                    onDeleteHistoryClick = {
                                        screenModel.deleteHistoryItem(user.id)
                                    }
                                )
                            }

                            if (uiState.isLoadingMore && !uiState.isHistoryMode) {
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
@Composable
private fun SearchBarInput(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current

    TextField(
        value = query,
        onValueChange = onQueryChange,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Normal,
            color = customColors.textPrimary
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = customColors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        },
        placeholder = {
            Text(
                text = stringResource(Res.string.common_search_placeholder),
                style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
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
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = customColors.gourmetOrange,
            focusedTextColor = customColors.textPrimary,
            unfocusedTextColor = customColors.textPrimary
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 8.dp)
            .height(50.dp)
    )
}

@Composable
private fun SearchUserItem(
    user: UserSearchResponse,
    isHistoryItem: Boolean,
    onUserClick: () -> Unit,
    onDeleteHistoryClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    val username = user.username
    val displayName = user.name
    val profileImage = user.profile

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
            if (displayName.isNotBlank()) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = customColors.textSecondary
                    )
                )
            }
        }

        if (isHistoryItem) {
            IconButton(
                onClick = onDeleteHistoryClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.search_remove_from_history),
                    tint = customColors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}