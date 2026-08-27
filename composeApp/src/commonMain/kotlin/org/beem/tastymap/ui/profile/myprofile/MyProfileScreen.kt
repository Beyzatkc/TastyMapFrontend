package org.beem.tastymap.ui.profile.myprofile

import TastyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.domain.model.UserProfile
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.TastyTheme

class MyProfileScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<MyProfileScreenModel>()
        val state by screenModel.myProfileState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val navigator = LocalNavigator.currentOrThrow

        var showChangePasswordSheet by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            screenModel.getProfileMock()
        }

        LaunchedEffect(state.errorMessage, state.successMessage) {
            state.errorMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
                screenModel.clearMessages()
            }
            state.successMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
                showChangePasswordSheet = false // Başarılı işlem sonrası sheet'i kapat
                screenModel.clearMessages()
            }
        }

        MyProfileContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onSettingsClick = {
                navigator.push(
                    SettingsScreen(
                        state = state,
                        onChangePasswordSubmit = { oldPassword, newPassword, againNew ->
                            screenModel.changePassword(oldPassword, newPassword, againNew)
                        },
                        onActiveDevicesClick = { },
                        onLogoutClick = { },
                        onClearMessages = { screenModel.clearMessages() }
                    )
                )
            },
            onEditProfileClick = { /* Düzenleme BottomSheet/Dialog aç */ }
        )

        // ŞİFRE DEĞİŞTİRME BOTTOM SHEET
        if (showChangePasswordSheet) {
            ChangePasswordBottomSheet(
                isActionLoading = state.isActionLoading,
                onDismissRequest = { showChangePasswordSheet = false },
                onSubmitClick = { oldPassword, newPassword, againNew ->
                    // Tüm kontrol ve doğrulamalar MyProfileScreenModel.changePassword() içinde gerçekleşir
                    screenModel.changePassword(
                        oldPassword = oldPassword,
                        newPassword = newPassword,
                        againNew = againNew
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileContent(
    state: MyProfileUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onSettingsClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {}
) {
    val customColors = LocalCustomColors.current
    val pageBackgroundColor = Color(0xFFFAFAF8)
    val darkHeaderColor = Color(0xFF18345A)
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = pageBackgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.profile?.username?.let { "@$it" } ?: "Profilim",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Ayarlar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkHeaderColor
                )
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = customColors.gourmetOrange)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(pageBackgroundColor)
                    .padding(innerPadding)
            ) {
                // 1. HERO BAŞLIK KARTI
                item {
                    Surface(
                        color = darkHeaderColor,
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(92.dp)
                                        .clip(CircleShape)
                                        .border(3.dp, customColors.gourmetOrange, CircleShape)
                                        .background(Color.DarkGray)
                                )

                                state.profile?.role?.let { role ->
                                    Surface(
                                        color = customColors.gourmetOrange,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.offset(y = 4.dp)
                                    ) {
                                        Text(
                                            text = role,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
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
                                text = state.profile?.name ?: "Benim Adım",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = state.profile?.biography ?: "Henüz bir biyografi eklemediniz.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            TastyButton(
                                text = "Profili Düzenle",
                                onClick = onEditProfileClick,
                                modifier = Modifier.fillMaxWidth(),
                                isPrimary = true,
                                isLoading = state.isActionLoading,
                                backcolor = customColors.gourmetOrange,
                                textcolor = Color.White,
                                strokecolor = Color.Transparent
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. METRİK KARTLARI
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            title = "Paylaşımım",
                            value = (state.profile?.postCount ?: 0).toString(),
                            modifier = Modifier.weight(1f),
                            cardColor = Color.LightGray.copy(alpha = 0.2f)
                        )
                        MetricCard(
                            title = "Takipçim",
                            value = (state.profile?.subscriberCount ?: 0).toString(),
                            modifier = Modifier.weight(1f),
                            cardColor = Color.LightGray.copy(alpha = 0.2f)
                        )
                        MetricCard(
                            title = "Takibim",
                            value = (state.profile?.subscribedCount ?: 0).toString(),
                            modifier = Modifier.weight(1f),
                            cardColor = Color.LightGray.copy(alpha = 0.2f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 3. TAB SEÇİM ALANI
                item {
                    Surface(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            TabButton(
                                text = "Paylaşımlarım",
                                icon = Icons.Default.GridOn,
                                isSelected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                modifier = Modifier.weight(1f),
                                activeColor = customColors.navy,
                                accentColor = customColors.gourmetOrange
                            )
                            TabButton(
                                text = "Haritam",
                                icon = Icons.Default.Map,
                                isSelected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                modifier = Modifier.weight(1f),
                                activeColor = customColors.navy,
                                accentColor = customColors.gourmetOrange
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 4. İÇERİK ALANI
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedTab == 0) {
                            Text(
                                text = "Paylaştığınız tüm lezzet incelemeleri burada listelenecek.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                            )
                        } else {
                            Text(
                                text = "Kaydettiğiniz ve işaretlediğiniz kişisel lezzet haritanız.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    cardColor: Color
) {
    Surface(
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
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.Black)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = Color.DarkGray,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color,
    accentColor: Color
) {
    Surface(
        color = if (isSelected) activeColor else Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isSelected) accentColor else Color.DarkGray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = if (isSelected) MaterialTheme.typography.titleMedium.copy(color = Color.White)
                else MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyProfileScreenPreview() {
    TastyTheme {
        MyProfileContent(
            state = MyProfileUiState(
                isLoading = false,
                profile = UserProfile(
                    userId = 1L,
                    username = "mryazilimci",
                    name = "Melih Öztürk",
                    profilePhoto = null,
                    role = "KULLANICI",
                    biography = "Android geliştirici & Lezzet avcısı 🎯",
                    postCount = 15,
                    subscriberCount = 230,
                    subscribedCount = 180,
                    blockedByMe = false,
                    blockedMe = false
                ),
                activeDeviceCount = 2
            )
        )
    }
}