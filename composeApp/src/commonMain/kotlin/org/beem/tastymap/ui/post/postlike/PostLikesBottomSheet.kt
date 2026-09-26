package org.beem.tastymap.ui.post.postlike

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import coil3.compose.AsyncImage
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.post.PostLikeUserResponse
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostLikesBottomSheet(
    onDismiss: () -> Unit,
    onUserClick: (Long) -> Unit,
    uiState: PostLikesUiState,
    onActionClick: (Long, RelationStatus) -> Unit,
    onLoadNextPage: () -> Unit,
    onRetry: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filteredList = remember(uiState.items, searchQuery) {
        if (searchQuery.isBlank()) uiState.items
        else uiState.items.filter { it.username.contains(searchQuery, ignoreCase = true) }
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
            !uiState.isLoading &&
                    !uiState.isLoadingMore &&
                    !uiState.isLoadingMoreError &&
                    uiState.errorMessage == null &&
                    !uiState.isLastPage &&
                    totalItems > 0 &&
                    lastVisibleItem >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
             onLoadNextPage()
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
                .fillMaxHeight(0.85f)
                .background(customColors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // BAŞLIK
            Text(
                text = stringResource(Res.string.likes_title), // "Beğenenler"
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = customColors.textPrimary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            HorizontalDivider(color = customColors.surfaceVariant)

            // ARAMA ÇUBUĞU
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                LikerSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            // İÇERİK DURUMLARI (Loading, Error, Empty, List)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                val isError = !uiState.errorMessage.isNullOrBlank()
                val isEmpty = uiState.items.isEmpty()

                when {
                    uiState.isLoading && isEmpty && !isError -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = customColors.gourmetOrange)
                        }
                    }

                    isError && isEmpty -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            FilledTonalButton(
                                onClick = {  onRetry()  },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = customColors.surfaceVariant,
                                    contentColor = customColors.textPrimary
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(Res.string.active_devices_retry))
                                }
                            }
                        }
                    }

                    filteredList.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (searchQuery.isNotBlank()) stringResource(Res.string.profile_no_results)
                                else stringResource(Res.string.profile_empty_list),
                                style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 32.dp)
                        ) {
                            items(filteredList, key = { it.userId }) { user ->
                                LikerUserItem(
                                    user = user,
                                    onUserClick = {
                                        onDismiss()
                                        onUserClick(user.userId)
                                    },
                                    onActionClick = {
                                         onActionClick(user.userId, user.relationStatus ?: RelationStatus.NOT_FOLLOWING,)
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
                            } else if (uiState.isLoadingMoreError) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        IconButton(
                                            onClick = {
                                                onLoadNextPage()
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
        }
    }
}
@Composable
private fun LikerSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
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
            Icon(Icons.Default.Search, contentDescription = null, tint = customColors.textSecondary)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = customColors.textSecondary)
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
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun LikerUserItem(
    user: PostLikeUserResponse,
    onUserClick: () -> Unit,
    onActionClick: () -> Unit
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
private data class ActionStyle(
    val text: String,
    val backColor: Color,
    val textColor: Color,
    val strokeColor: Color,
    val isPrimary: Boolean
)