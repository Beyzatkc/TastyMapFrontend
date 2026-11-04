package org.beem.tastymap.ui.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.beem.tastymap.LocalIsWideScreen
import org.beem.tastymap.ui.ai.components.*
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AiChatScreen(
    navController: NavController,
    showBackButton: Boolean = true
) {
    AdjustResizeEffect()

    val viewModel = koinViewModel<AiViewModel>()
    val state by viewModel.state.collectAsState()
    val customColors = LocalCustomColors.current
    val isWideScreen = LocalIsWideScreen.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0

    // Oturum DTO listesini Sidebar modeline bağlama
    val sidebarSessions = remember(state.sessions) {
        state.sessions.map { dto ->
            ChatSessionItem(
                sessionId = dto.sessionId,
                title = dto.title,
                dateText = dto.updatedAt
            )
        }
    }

    // Yeni mesaj geldiğinde veya klavye açıldığında en alta kaydırma
    LaunchedEffect(isImeVisible, state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    // Tek noktadan Intent Yönetimi
    val onIntent: (AiChatIntent) -> Unit = { intent ->
        when (intent) {
            is AiChatIntent.ToggleSidebar -> {
                scope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            }
            is AiChatIntent.SelectSession, is AiChatIntent.NewChat -> {
                scope.launch { drawerState.close() }
                viewModel.onIntent(intent)
            }
            is AiChatIntent.NavigateBack -> {
                navController.popBackStack()
            }
            is AiChatIntent.PlaceClicked -> {
                // İleride mekan sayfasına geçiş: navController.navigate("place/${intent.placeDbId}")
                viewModel.onIntent(intent)
            }
            else -> {
                viewModel.onIntent(intent)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isWideScreen,
        drawerContent = {
            if (!isWideScreen) {
                ModalDrawerSheet(drawerContainerColor = customColors.darkHeaderColor) {
                    AiChatSidebar(
                        sessions = sidebarSessions,
                        selectedSessionId = state.currentSessionId,
                        remainingQuotaPercentage = 100.0, // Kota servisi entegre edilene kadar
                        customColors = customColors,
                        onIntent = onIntent
                    )
                }
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(customColors.surface)
        ) {
            // Geniş ekranda sol panel kalıcı durur
            if (isWideScreen) {
                AiChatSidebar(
                    sessions = sidebarSessions,
                    selectedSessionId = state.currentSessionId,
                    remainingQuotaPercentage = 100.0,
                    customColors = customColors,
                    onIntent = onIntent
                )
            }

            // Ana Sohbet Alanı
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Mobilde sidebar'ı açmak için menü butonu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // SOL: Menü Butonu + Model Seçici
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!isWideScreen) {
                                IconButton(
                                    onClick = { onIntent(AiChatIntent.ToggleSidebar) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menü",
                                        tint = customColors.textPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            // ARTIK MENÜNÜN HEMEN YANINDA DOĞRU YERİNDE
                            AiProviderSelector(
                                selectedProvider = state.selectedProvider,
                                customColors = customColors,
                                onIntent = onIntent
                            )
                        }

                        // SAĞ: Hızlıca Yeni Sohbet Açma Butonu
                        IconButton(
                            onClick = { onIntent(AiChatIntent.NewChat) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Yeni Sohbet",
                                tint = customColors.textSecondary
                            )
                        }
                    }

                    // Mesajlar Yüklenirken Loading
                    if (state.isMessagesLoading) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = customColors.gourmetOrange)
                        }
                    } else if (state.messages.isEmpty()) {
                        AiWelcomeView(
                            customColors = customColors,
                            onIntent = onIntent,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(bottom = 80.dp)
                        )
                    } else {
                        // Mesaj Akışı
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = if (isWideScreen) 80.dp else 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(
                                top = 16.dp,
                                bottom = if (state.previewPlace != null) 240.dp else 100.dp
                            )
                        ) {
                            items(state.messages, key = { it.id }) { message ->
                                AiChatBubble(
                                    message = message,
                                    customColors = customColors,
                                    onIntent = onIntent
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    // 1. ÜSTTE BELİREN MEKAN SPOTLIGHT KARTI
                    AnimatedVisibility(
                        visible = state.previewPlace != null,
                        enter = slideInVertically { it } + androidx.compose.animation.fadeIn(),
                        exit = slideOutVertically { it } + androidx.compose.animation.fadeOut()
                    ) {
                        state.previewPlace?.let { place ->
                            PlacePreviewFloatingCard(
                                place = place,
                                userLat = state.userLatitude,
                                userLng = state.userLongitude,
                                customColors = customColors,
                                onIntent = onIntent
                            )
                        }
                    }

                    // 2. EN ALTTAKİ FLOATING INPUT KUTUSU
                    AiFloatingInputBox(
                        inputText = state.inputText,
                        isStreaming = state.isStreaming,
                        isWideScreen = isWideScreen,
                        customColors = customColors,
                        onIntent = onIntent
                    )
                }
            }
        }
    }
}