package org.beem.tastymap.ui.post.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.post.PostAndVisitRequest
import org.beem.tastymap.data.model.visit.VisitResponse
import org.beem.tastymap.ui.components.TastyButton
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.add_photo
import tastymap.composeapp.generated.resources.allow_comments
import tastymap.composeapp.generated.resources.change
import tastymap.composeapp.generated.resources.comments
import tastymap.composeapp.generated.resources.create_post_title
import tastymap.composeapp.generated.resources.no_saved_visits
import tastymap.composeapp.generated.resources.place_about_placeholder
import tastymap.composeapp.generated.resources.select_place
import tastymap.composeapp.generated.resources.select_visit_to_share
import tastymap.composeapp.generated.resources.selected_place
import tastymap.composeapp.generated.resources.share_post
import tastymap.composeapp.generated.resources.tap_to_upload_photo
import tastymap.composeapp.generated.resources.your_visits

class CreatePostScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val screenModel = koinScreenModel<CreatePostScreenModel>()
        val uiState by screenModel.uiState.collectAsState()

        val customColors = LocalCustomColors.current
        val navigator = LocalNavigator.currentOrThrow

        var selectedVisit by remember { mutableStateOf<VisitResponse?>(null) }
        var explanation by remember { mutableStateOf("") }
        var photoUrl by remember { mutableStateOf<String?>(null) }
        var commentEnabled by remember { mutableStateOf(true) }

        // BottomSheet State'i
        var showVisitSheet by remember { mutableStateOf(false) }

        LaunchedEffect(uiState.generalError) {
            uiState.generalError?.let { message ->
                ToastManager.show(message)
            }
        }

        LaunchedEffect(uiState.success) {
            uiState.success?.let { message ->
                // YÖNLENDİRME YAPILACAK
            }
        }

        Scaffold(
            containerColor = customColors.background,

            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.create_post_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = customColors.textPrimary
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = customColors.background
                    )
                )
            },

            bottomBar = {
                AnimatedVisibility(
                    visible = selectedVisit != null
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(customColors.background)
                            .padding(16.dp)
                    ) {
                        TastyButton(
                            text = stringResource(Res.string.share_post),
                            onClick = {
                                selectedVisit?.let { visit ->

                                    val request = PostAndVisitRequest(
                                        placeId = visit.placeId,
                                        placeName = visit.placeName,
                                        categories = visit.categories,
                                        city = visit.city,
                                        district = visit.district,
                                        neighbourhood = visit.neighbourhood,
                                        latitude = visit.latitude,
                                        longitude = visit.longitude,
                                        averagePoint = visit.averagePoint,
                                        isWantToPost = true,
                                        explanation = explanation.ifBlank { null },
                                        photoUrl = photoUrl,
                                        commentEnabled = commentEnabled
                                    )

                                    screenModel.addPost(
                                        request = request
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = uiState.isLoading,
                            backcolor = customColors.navy,
                            textcolor = Color.White
                        )
                    }
                }
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // 1. Ziyaret Seçici Kartı
                VisitSelectorCard(
                    selectedVisit = selectedVisit,
                    customColors = customColors,
                    onClick = {
                        showVisitSheet = true
                    }
                )

                // 2. Gizli Form Alanı
                AnimatedVisibility(
                    visible = selectedVisit != null,
                    enter = fadeIn() + expandVertically()
                ) {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {

                        PhotoUploadArea(
                            customColors = customColors
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            TastyTextField(
                                value = explanation,
                                onValueChange = {
                                    if (it.length <= 500) {
                                        explanation = it

                                        if (uiState.explanationError != null) {
                                            screenModel.clearErrors()
                                        }
                                    }
                                },
                                label = stringResource(
                                    Res.string.place_about_placeholder
                                ),
                                singleLine = false,
                                maxLines = 5,
                                error = uiState.explanationError
                            )

                            // Karakter Sayacı
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
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
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 16.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
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
                                onCheckedChange = {
                                    commentEnabled = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = customColors.surface,
                                    checkedTrackColor = customColors.navy,
                                    uncheckedThumbColor = customColors.surface,
                                    uncheckedTrackColor = customColors.borderStrong,
                                    uncheckedBorderColor = customColors.borderStrong
                                )
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(60.dp)
                        )
                    }
                }
            }
        }

        // --- ZİYARET SEÇİMİ BOTTOM SHEET ---
        if (showVisitSheet) {

            ModalBottomSheet(
                onDismissRequest = {
                    showVisitSheet = false
                },
                containerColor = customColors.background
            ) {

                Text(
                    text = stringResource(Res.string.your_visits),
                    style = MaterialTheme.typography.titleMedium,
                    color = customColors.textPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(
                        bottom = 32.dp,
                        top = 8.dp,
                        start = 20.dp,
                        end = 20.dp
                    )
                ) {

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    if (uiState.visits.isEmpty()) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = stringResource(
                                    Res.string.no_saved_visits
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = customColors.textSecondary
                            )
                        }

                    } else {

                        LazyColumn {

                            items(uiState.visits) { visit ->

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {

                                            selectedVisit = visit
                                            showVisitSheet = false
                                        }
                                        .padding(
                                            vertical = 12.dp,
                                            horizontal = 8.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = null,
                                        tint = customColors.navy
                                    )

                                    Spacer(
                                        modifier = Modifier.width(12.dp)
                                    )

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {

                                        Text(
                                            text = visit.placeName,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = customColors.textPrimary
                                        )

                                        Text(
                                            text = "${visit.district ?: ""}, ${visit.city ?: ""}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = customColors.textSecondary
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    color = customColors.borderLight,
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// --------------------------------------------------
// ALT BİLEŞENLER
// --------------------------------------------------

@Composable
fun VisitSelectorCard(
    selectedVisit: VisitResponse?,
    customColors: CustomColors,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = customColors.surface
        ),
        border = BorderStroke(
            1.dp,
            customColors.borderLight
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(customColors.wave),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = customColors.navy
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                if (selectedVisit != null) {

                    Text(
                        text = stringResource(
                            Res.string.selected_place
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = customColors.navy
                        )
                    )

                    Text(
                        text = selectedVisit.placeName,
                        style = MaterialTheme.typography.titleMedium,
                        color = customColors.textPrimary
                    )

                    Text(
                        text = "${selectedVisit.district ?: ""}, ${selectedVisit.city ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = customColors.textSecondary
                    )

                } else {

                    Text(
                        text = stringResource(
                            Res.string.select_place
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = customColors.textPrimary
                    )

                    Text(
                        text = stringResource(
                            Res.string.select_visit_to_share
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = customColors.textSecondary
                    )
                }
            }

            Icon(
                Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(
                    Res.string.change
                ),
                tint = customColors.textTertiary
            )
        }
    }
}


@Composable
fun PhotoUploadArea(
    customColors: CustomColors
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(
                elevation = 4.dp, // Gölgenin derinliği (yüksekliği)
                shape = RoundedCornerShape(16.dp),
                spotColor = customColors.borderLight // İsteğe bağlı gölge rengi tonu
            )
            .clip(RoundedCornerShape(16.dp))
            .background(customColors.surface)
            .clickable {
                /* Fotoğraf Seçici */
            },
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Icon(
                imageVector = Icons.Default.AddPhotoAlternate,
                contentDescription = stringResource(
                    Res.string.add_photo
                ),
                modifier = Modifier.size(48.dp),
                tint = customColors.placeHolderIcon
            )

            Text(
                text = stringResource(
                    Res.string.tap_to_upload_photo
                ),
                style = MaterialTheme.typography.titleMedium,
                color = customColors.placeHolderIcon
            )
        }
    }
}