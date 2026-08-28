package org.beem.tastymap.ui.profile.myprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class SettingsScreen(
    private val onActiveDevicesClick: () -> Unit,
    private val onLogoutClick: () -> Unit
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // ViewModel'i Koin üzerinden alıyoruz (Voyager kilitlenmelerini önler)
        val screenModel = koinScreenModel<MyProfileScreenModel>()
        val state by screenModel.myProfileState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow
        val darkHeaderColor = Color(0xFF18345A)
        val pageBackgroundColor = Color(0xFFFAFAF8)
        val snackbarHostState = remember { SnackbarHostState() }

        var showChangePasswordSheet by remember { mutableStateOf(false) }

        // SADECE successMessage Dolduğunda Kapanır (Hata olunca bu blok çalışmaz!)
        LaunchedEffect(state.successMessage) {
            println("SUCCESS MESSAGE = ${state.successMessage}")
            println("ERROR MESSAGE = ${state.errorMessage}")

            state.successMessage?.let { message ->
                showChangePasswordSheet = false
                snackbarHostState.showSnackbar(message)
                screenModel.clearMessages()
            }
        }


        Scaffold(
            containerColor = pageBackgroundColor,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Ayarlar ve Hareketler",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = darkHeaderColor)
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Hesabınız",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            SettingsOptionItem(
                                icon = Icons.Default.Lock,
                                title = "Şifre Değiştir",
                                onClick = {
                                    screenModel.clearMessages()
                                    showChangePasswordSheet = true
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Color.LightGray.copy(alpha = 0.3f)
                            )
                            SettingsOptionItem(
                                icon = Icons.Default.Devices,
                                title = "Aktif Cihazlar",
                                badgeText = "${state.activeDeviceCount}",
                                onClick = onActiveDevicesClick
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Giriş",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        SettingsOptionItem(
                            icon = Icons.AutoMirrored.Filled.Logout,
                            title = "Çıkış Yap",
                            textColor = MaterialTheme.colorScheme.error,
                            iconColor = MaterialTheme.colorScheme.error,
                            showChevron = false,
                            onClick = onLogoutClick
                        )
                    }
                }
            }

            if (showChangePasswordSheet) {
                ChangePasswordBottomSheet(
                    isActionLoading = state.isActionLoading,
                    errorMessage = state.errorMessage,
                    onDismissRequest = {
                        showChangePasswordSheet = false
                        screenModel.clearMessages()
                    },
                    onSubmitClick = { oldPassword, newPassword, againNew ->
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
}
@Composable
private fun SettingsOptionItem(
    icon: ImageVector,
    title: String,
    badgeText: String? = null,
    textColor: Color = Color.Unspecified,
    iconColor: Color = Color.DarkGray,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.weight(1f)
        )
        if (badgeText != null) {
            Surface(
                color = Color.LightGray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        if (showChevron) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}