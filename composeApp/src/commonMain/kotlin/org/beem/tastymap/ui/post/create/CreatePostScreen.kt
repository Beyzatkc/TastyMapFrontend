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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.launch
import org.beem.tastymap.core.camera.rememberCameraLauncher
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
import tastymap.composeapp.generated.resources.camera
import tastymap.composeapp.generated.resources.change
import tastymap.composeapp.generated.resources.comments
import tastymap.composeapp.generated.resources.create_post_title
import tastymap.composeapp.generated.resources.no_saved_visits
import tastymap.composeapp.generated.resources.notification_retry
import tastymap.composeapp.generated.resources.place_about_placeholder
import tastymap.composeapp.generated.resources.post_description_hint
import tastymap.composeapp.generated.resources.profile_action_reject
import tastymap.composeapp.generated.resources.select_photo_source
import tastymap.composeapp.generated.resources.select_place
import tastymap.composeapp.generated.resources.select_visit_to_share
import tastymap.composeapp.generated.resources.selected_place
import tastymap.composeapp.generated.resources.share_post
import tastymap.composeapp.generated.resources.tap_to_upload_photo
import tastymap.composeapp.generated.resources.verify_error_default
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
        var commentEnabled by remember { mutableStateOf(true) }

        var showVisitSheet by remember { mutableStateOf(false) }
        var showImagePickerSheet by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()
        var selectedImagesBytes by remember { mutableStateOf<List<ByteArray>>(emptyList()) }


        val remainingSlots = 3 - selectedImagesBytes.size

        val galleryLauncher = rememberFilePickerLauncher(
            type = PickerType.Image,
            mode = PickerMode.Multiple(maxItems = remainingSlots.coerceAtLeast(1))
        ) { files ->
            files?.let { newFiles ->
                val remaining = 3 - selectedImagesBytes.size

                if (remaining <= 0) return@rememberFilePickerLauncher

                scope.launch {
                    val newBytes = newFiles
                        .take(remaining)
                        .map { it.readBytes() }

                    selectedImagesBytes =
                        selectedImagesBytes + newBytes
                }
            }
        }


        val cameraLauncher = rememberCameraLauncher { bytes ->
            bytes?.let {
                if (selectedImagesBytes.size < 3) {
                    selectedImagesBytes = selectedImagesBytes + it
                }
            }
        }


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
                                        photoUrl = emptyList(),
                                        commentEnabled = commentEnabled
                                    )
                                    screenModel.addPost(
                                        request = request,
                                        selectedImagesBytes = selectedImagesBytes
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


                VisitSelectorCard(
                    selectedVisit = selectedVisit,
                    customColors = customColors,
                    onClick = {
                        showVisitSheet = true
                    }
                )

                AnimatedVisibility(
                    visible = selectedVisit != null,
                    enter = fadeIn() + expandVertically()
                ) {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {

                        PhotoUploadArea(
                            customColors = customColors,
                            error = uiState.photoError?.let { stringResource(it) },
                            selectedImagesBytes = selectedImagesBytes,
                            onAddClick = {
                                showImagePickerSheet = true
                            },
                            onRemoveClick = { index ->
                                if (index in selectedImagesBytes.indices) {
                                    selectedImagesBytes = selectedImagesBytes.toMutableList().apply {
                                        removeAt(index)
                                    }
                                }
                            }
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = customColors.surface
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    customColors.borderLight
                                )
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

                                                if (uiState.explanationError != null) {
                                                    screenModel.clearErrors()
                                                }
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

        if (showVisitSheet) {
            ModalBottomSheet(
                onDismissRequest = { showVisitSheet = false },
                containerColor = customColors.background,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.75f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(Res.string.your_visits),
                            style = MaterialTheme.typography.titleLarge,
                            color = customColors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(Res.string.select_visit_to_share),
                            style = MaterialTheme.typography.bodyMedium,
                            color = customColors.textSecondary
                        )
                    }

                    when {
                        uiState.isVisitsLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = customColors.navy)
                            }
                        }

                        uiState.generalError != null -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = customColors.error,
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = uiState.generalError ?: stringResource(Res.string.verify_error_default),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = customColors.textSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                FilledTonalButton(
                                    onClick = { screenModel.loadInitialVisits() },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = customColors.surfaceVariant,
                                        contentColor = customColors.textPrimary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(Res.string.notification_retry),
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                        uiState.visits.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(Res.string.no_saved_visits),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = customColors.textSecondary
                                )
                            }
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                itemsIndexed(uiState.visits) { index, visit ->

                                    if (index >= uiState.visits.size - 2) {
                                        LaunchedEffect(Unit) {
                                            screenModel.loadMoreVisits()
                                        }
                                    }

                                    val isSelected = selectedVisit?.visitId == visit.visitId

                                    VisitListItemCard(
                                        visit = visit,
                                        isSelected = isSelected,
                                        customColors = customColors,
                                        onClick = {
                                            selectedVisit = visit
                                            showVisitSheet = false
                                        }
                                    )
                                }

                                if (uiState.isLoadingMoreVisits) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = customColors.navy,
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                }

                                if (uiState.loadMoreError != null) {
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = uiState.loadMoreError ?: "",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = customColors.error
                                            )
                                            TextButton(onClick = { screenModel.loadMoreVisits() }) {
                                                Text(stringResource(Res.string.notification_retry), color = customColors.navy)
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
        if (showImagePickerSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showImagePickerSheet = false
                },
                containerColor = customColors.background,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp, top = 8.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.add_photo),
                        style = MaterialTheme.typography.titleLarge,
                        color = customColors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(Res.string.select_photo_source),
                        style = MaterialTheme.typography.bodyMedium,
                        color = customColors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ImagePickerOptionCard(
                            icon = Icons.Default.CameraAlt,
                            title = stringResource(Res.string.camera),
                            customColors = customColors,
                            onClick = {
                                showImagePickerSheet = false
                                cameraLauncher.launch()
                            }
                        )
                        ImagePickerOptionCard(
                            icon = Icons.Default.PhotoLibrary,
                            title = "Galeri",
                            customColors = customColors,
                            onClick = {
                                showImagePickerSheet = false
                                galleryLauncher.launch()
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ImagePickerOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    customColors: CustomColors,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(customColors.wave),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = customColors.navy,
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = customColors.textPrimary
        )
    }
}
@Composable
private fun VisitListItemCard(
    visit: VisitResponse,
    isSelected: Boolean,
    customColors: CustomColors,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) customColors.gourmetOrange.copy(alpha = 0.08f) else customColors.surface
    val borderColor = if (isSelected) customColors.gourmetOrange else customColors.borderLight

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // İkon Kutusu
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) customColors.gourmetOrange else customColors.wave),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else customColors.navy,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Metin Alanı
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = visit.placeName,
                    style = MaterialTheme.typography.titleMedium,
                    color = customColors.textPrimary,
                    maxLines = 1
                )

                val locationText = listOfNotNull(visit.district, visit.city)
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

            Spacer(modifier = Modifier.width(8.dp))

            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = customColors.gourmetOrange,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = customColors.textTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

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
    customColors: CustomColors,
    error: String? = null,
    selectedImagesBytes: List<ByteArray> = emptyList(),
    onAddClick: () -> Unit,
    onRemoveClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            itemsIndexed(
                items = selectedImagesBytes,
                key = { index, _ -> index }
            ) { index, bytes ->
                Box(
                    modifier = Modifier
                        .size(140.dp, 180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(customColors.surface)
                ) {
                    coil3.compose.AsyncImage(
                        model = bytes,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )

                    // Fotoğraf Silme Butonu
                    IconButton(
                        onClick = { onRemoveClick(index) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(28.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.profile_action_reject),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (selectedImagesBytes.size < 3) {
                item {
                    Box(
                        modifier = Modifier
                            .size(140.dp, 180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(customColors.surface)
                            .border(
                                width = if (error != null) 1.5.dp else 1.dp,
                                color = if (error != null) customColors.error else customColors.borderLight,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(onClick = onAddClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = stringResource(Res.string.add_photo),
                                modifier = Modifier.size(36.dp),
                                tint = if (error != null) customColors.error else customColors.placeHolderIcon
                            )
                            Text(
                                text = "Fotoğraf Ekle\n(${selectedImagesBytes.size}/3)",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (error != null) customColors.error else customColors.placeHolderIcon,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Hata Mesajı Görünümü
        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn() + expandVertically()
        ) {
            error?.let { errorMessage ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = customColors.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.error
                    )
                }
            }
        }
    }
}