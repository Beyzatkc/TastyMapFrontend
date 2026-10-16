package org.beem.tastymap.ui.post.mypost.updatepost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.post.PostResponse
import org.beem.tastymap.data.model.post.PostUpdateRequest
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.components.responsiveContentWidth
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.allow_comments
import tastymap.composeapp.generated.resources.cannot_be_changed
import tastymap.composeapp.generated.resources.comments
import tastymap.composeapp.generated.resources.edit_post_success
import tastymap.composeapp.generated.resources.edit_post_title
import tastymap.composeapp.generated.resources.edit_profile_save
import tastymap.composeapp.generated.resources.photos_title
import tastymap.composeapp.generated.resources.place_about_placeholder
import tastymap.composeapp.generated.resources.post_description_hint
import tastymap.composeapp.generated.resources.profile_back_cd

class EditPostScreen(val postId: Long) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<EditPostScreenModel>()
        val uiState by screenModel.uiState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow
        val customColors = LocalCustomColors.current

        var explanation by remember { mutableStateOf("") }
        var commentEnabled by remember { mutableStateOf(true) }
        var isFormInitialized by remember { mutableStateOf(false) }
        var isSuccessAnimated by remember { mutableStateOf(false) }

        LaunchedEffect(postId) {
            screenModel.loadPostDetail(postId)
        }

        LaunchedEffect(uiState.post) {
            uiState.post?.let { post ->
                if (!isFormInitialized) {
                    explanation = post.explanation.orEmpty()
                    commentEnabled = post.isCommentEnabled
                    isFormInitialized = true
                }
            }
        }

        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { message ->
                ToastManager.show(message)
            }
        }

        LaunchedEffect(uiState.successMessageRes) {
            if (uiState.successMessageRes != null) {
                isSuccessAnimated = true
                delay(600)
                navigator.pop()
            }
        }

        Scaffold(
            containerColor = customColors.background,
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(Res.string.edit_post_title),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = customColors.textPrimary
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
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = customColors.background
                        )
                    )

                }
            },
            bottomBar = {
                if (uiState.post != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .responsiveContentWidth()
                                .background(customColors.background)
                                .padding(16.dp)
                        ) {
                            TastyButton(
                                text = stringResource(Res.string.edit_profile_save),
                                onClick = {
                                    val request = PostUpdateRequest(
                                        explanation = explanation.ifBlank { null },
                                        commentEnabled = commentEnabled
                                    )
                                    screenModel.updatePost(postId, request)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isLoading = uiState.isActionLoading,
                                backcolor = customColors.navy,
                                textcolor = Color.White
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {

                when {
                    uiState.isLoading && uiState.post == null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = customColors.navy)
                        }
                    }

                    uiState.post != null -> {
                        val post = uiState.post!!

                        Column(
                            modifier = Modifier
                                .responsiveContentWidth()
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {

                            ReadOnlyPlaceCard(
                                post = post,
                                customColors = customColors
                            )

                            if (post.photoUrls.isNotEmpty()) {
                                ReadOnlyPhotosSection(
                                    photos = post.photoUrls,
                                    customColors = customColors
                                )
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = customColors.surface
                                ),
                                border = BorderStroke(1.dp, customColors.borderLight)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = stringResource(Res.string.place_about_placeholder),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = customColors.textPrimary
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = stringResource(Res.string.post_description_hint),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = customColors.textSecondary
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    TastyTextField(
                                        value = explanation,
                                        onValueChange = {
                                            if (it.length <= 500) {
                                                explanation = it
                                            }
                                        },
                                        label = "",
                                        singleLine = false,
                                        maxLines = 5,
                                        error = uiState.explanationError?.let { stringResource(it) }
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = "${explanation.length}/500",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (explanation.length >= 500) {
                                                customColors.error
                                            } else {
                                                customColors.textSecondary
                                            }
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(customColors.surface)
                                    .border(
                                        1.dp,
                                        customColors.borderLight,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = stringResource(Res.string.comments),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = customColors.textPrimary
                                    )

                                    Text(
                                        text = stringResource(Res.string.allow_comments),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = customColors.textSecondary
                                    )
                                }

                                Switch(
                                    checked = commentEnabled,
                                    onCheckedChange = { commentEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = customColors.surface,
                                        checkedTrackColor = customColors.navy,
                                        uncheckedThumbColor = customColors.surface,
                                        uncheckedTrackColor = customColors.borderStrong,
                                        uncheckedBorderColor = customColors.borderStrong
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isSuccessAnimated,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(200)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(customColors.background.copy(alpha = 0.95f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(customColors.gourmetOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.successMessageRes?.let { stringResource(it) } ?: stringResource(Res.string.edit_post_success),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = customColors.textPrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyPlaceCard(
    post: PostResponse,
    customColors: CustomColors
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.surface),
        border = BorderStroke(1.dp, customColors.borderLight)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(customColors.gourmetOrange),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = post.placeName.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    color = customColors.textPrimary,
                    maxLines = 1
                )

                val locationText = listOfNotNull(post.district, post.city)
                    .filter { it.isNotBlank() }
                    .joinToString(", ")

                if (locationText.isNotEmpty()) {
                    Text(
                        text = locationText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = customColors.textSecondary,
                        maxLines = 1
                    )
                }
            }

            post.averagePoint.let { point ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(customColors.gourmetOrange.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = customColors.gourmetOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = point.toString(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = customColors.gourmetOrange
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReadOnlyPhotosSection(
    photos: List<String>,
    customColors: CustomColors
) {
    val pagerState = rememberPagerState(pageCount = { photos.size })

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.photos_title),
                style = MaterialTheme.typography.titleSmall,
                color = customColors.textPrimary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = customColors.textTertiary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = stringResource(Res.string.cannot_be_changed),
                    style = MaterialTheme.typography.labelSmall,
                    color = customColors.textTertiary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                //.height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, customColors.borderLight, RoundedCornerShape(16.dp))
                .background(customColors.surface)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = photos[page],
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (photos.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(photos.size) { index ->
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
    }
}