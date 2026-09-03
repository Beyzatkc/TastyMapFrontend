package org.beem.tastymap.ui.profile.subscribers

import TastyButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import org.beem.tastymap.data.model.subscribers.SubscribeResponse
import org.beem.tastymap.data.model.subscribers.SubscribeStatus
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.TastyTheme
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.common_search_placeholder
import tastymap.composeapp.generated.resources.profile_action_follow
import tastymap.composeapp.generated.resources.profile_action_following
import tastymap.composeapp.generated.resources.profile_action_requested
import tastymap.composeapp.generated.resources.profile_back_cd
import tastymap.composeapp.generated.resources.profile_connections_title
import tastymap.composeapp.generated.resources.profile_empty_list
import tastymap.composeapp.generated.resources.profile_metric_following
import tastymap.composeapp.generated.resources.profile_metric_subscribers
import tastymap.composeapp.generated.resources.profile_no_results
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

        var selectedTab by remember { mutableStateOf(initialTab) }
        var searchQuery by remember { mutableStateOf("") }
        val pullToRefreshState = rememberPullToRefreshState()
        val listState = rememberLazyListState()

        LaunchedEffect(userId, selectedTab) {
            screenModel.loadInitialData(userId, selectedTab)
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

                    TabRow(
                        selectedTabIndex = selectedTab.ordinal,
                        containerColor = customColors.background,
                        contentColor = customColors.gourmetOrange,
                        divider = {}
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
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(customColors.background)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(16.dp)
                )

                PullToRefreshBox(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { screenModel.refresh(userId) },
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
                    if (uiState.isLoading && uiState.items.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = customColors.gourmetOrange)
                        }
                    } else if (filteredList.isEmpty()) {
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
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(
                                items = filteredList,
                                key = { it.id }
                            ) { user ->
                                SubscriberUserItem(
                                    user = user,
                                    onUserClick = {
                                        // Navigator ile profiline git
                                    },
                                    onActionClick = {
                                        // Takip Et / Kaldır İşlemi
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
private fun SubscriberUserItem(
    user: SubscribeResponse,
    onUserClick: () -> Unit,
    onActionClick: () -> Unit = {}
) {
    val customColors = LocalCustomColors.current

    // Butonun dinamik durum ayarları
    val (buttonText, backColor, textColor, strokeColor, isPrimary) = when {
        user.relationStatus == SubscribeStatus.ACCEPTED -> {
            ActionStyle(
                stringResource(Res.string.profile_action_following),
                Color.Transparent,
                customColors.textPrimary,
                customColors.textSecondary.copy(alpha = 0.4f),
                false
            )
        }
        user.relationStatus == SubscribeStatus.PENDING -> {
            ActionStyle(
                stringResource(Res.string.profile_action_requested),
                Color.Transparent,
                customColors.textSecondary,
                customColors.textSecondary.copy(alpha = 0.3f),
                false
            )
        }
        else -> {
            ActionStyle(
                stringResource(Res.string.profile_action_follow),
                customColors.gourmetOrange,
                Color.White,
                Color.Transparent,
                true
            )
        }
    }

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

        TastyButton(
            text = buttonText,
            onClick = onActionClick,
            modifier = Modifier.width(130.dp),
            isPrimary = isPrimary,
            backcolor = backColor,
            textcolor = textColor,
            strokecolor = strokeColor
        )
    }
}

private data class ActionStyle(
    val text: String,
    val backColor: Color,
    val textColor: Color,
    val strokeColor: Color,
    val isPrimary: Boolean
)
@Preview(showBackground = true, name = "Açık Tema - Takipçiler Örneği")
@Composable
private fun SubscriberUserItemPreviewLight() {
    TastyTheme(useDarkTheme = false) {
        Surface(color = LocalCustomColors.current.background) {
            Column {
                // Takipçi sekmesi (Kaldır butonu)
                SubscriberUserItem(
                    user = SubscribeResponse(
                        id = 1,
                        username = "ahmet_yilmaz",
                        profile = null,
                        relationStatus = SubscribeStatus.ACCEPTED
                    ),
                    onUserClick = {},
                    onActionClick = {}
                )

                HorizontalDivider(color = LocalCustomColors.current.surfaceVariant)

                // Takip Edilenler sekmesi - Takip Ediliyor durumu
                SubscriberUserItem(
                    user = SubscribeResponse(
                        id = 2,
                        username = "mehmet_kaya",
                        profile = null,
                        relationStatus = SubscribeStatus.ACCEPTED
                    ),
                    onUserClick = {},
                    onActionClick = {}
                )
                HorizontalDivider(color = LocalCustomColors.current.surfaceVariant)

                // Takip Edilenler sekmesi - Takip Ediliyor durumu
                SubscriberUserItem(
                    user = SubscribeResponse(
                        id = 2,
                        username = "emrullah_kaya",
                        profile = null,
                        relationStatus = SubscribeStatus.PENDING
                    ),
                    onUserClick = {},
                    onActionClick = {}
                )

                HorizontalDivider(color = LocalCustomColors.current.surfaceVariant)

                // Takip Et durumu
                SubscriberUserItem(
                    user = SubscribeResponse(
                        id = 3,
                        username = "ayse_demir",
                        profile = null,
                        relationStatus = null
                    ),
                    onUserClick = {},
                    onActionClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Arama Çubuğu Örneği")
@Composable
private fun SearchBarPreview() {
    TastyTheme {
        Surface(color = LocalCustomColors.current.background) {
            Box(modifier = Modifier.padding(16.dp)) {
                SearchBar(
                    query = "Arama metni",
                    onQueryChange = {}
                )
            }
        }
    }
}

