package org.beem.tastymap.ui.profile.myprofile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.profile.myprofile.settings.activedevices.ActiveDevicesScreen
import org.beem.tastymap.ui.profile.myprofile.settings.changepassword.ChangePasswordScreenModel
import org.beem.tastymap.ui.profile.myprofile.settings.edithealth.EditHealthScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.TastyTheme
import kotlin.time.Clock

class SettingsScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val changePasswordScreenModel = koinScreenModel<ChangePasswordScreenModel>()
        val changePasswordState by changePasswordScreenModel.uiState.collectAsState()

        val settingsScreenModel = koinScreenModel<SettingsScreenModel>()
        val settingsState by settingsScreenModel.uiState.collectAsState()

        val isDarkModePref by settingsScreenModel.isDarkMode.collectAsState()
        val isDarkModeActive = isDarkModePref ?: isSystemInDarkTheme()

        val customColors = LocalCustomColors.current
        val navigator = LocalNavigator.currentOrThrow

        var showChangePasswordSheet by remember { mutableStateOf(false) }
        var showLogoutDialog by remember { mutableStateOf(false) }
        var isNotificationsEnabled by remember { mutableStateOf(true) }
        var mod by remember { mutableStateOf(false) }
        var isAccountPrivate by remember { mutableStateOf(false) }

        LaunchedEffect(changePasswordState.successMessage) {
            changePasswordState.successMessage?.let { message ->
                showChangePasswordSheet = false
                ToastManager.show(message)
                changePasswordScreenModel.clearMessages()
            }
        }

        LaunchedEffect(settingsState.isLoggedOut) {
            if (settingsState.isLoggedOut) {
                showLogoutDialog = false
                navigator.replaceAll(LogRegScreen())
            }
        }

        LaunchedEffect(settingsState.errorMessage) {
            settingsState.errorMessage?.let { message ->
                ToastManager.show(message)
                settingsScreenModel.clearMessages()
            }
        }

        Scaffold(
            containerColor = customColors.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Ayarlar ve Hareketler",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = customColors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = customColors.textPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = customColors.background
                    )
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
                // 1. Hesabım & Güvenlik
                item {
                    SettingsSectionHeader(title = "Hesabım & Güvenlik")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            SettingsOptionItem(
                                icon = Icons.Default.Lock,
                                title = "Şifre Değiştir",
                                onClick = {
                                    changePasswordScreenModel.clearMessages()
                                    showChangePasswordSheet = true
                                }
                            )
                            SettingsDivider()
                            SettingsOptionItem(
                                icon = Icons.Default.Devices,
                                title = "Aktif Cihazlar",
                                onClick = {
                                    navigator.push(ActiveDevicesScreen())
                                }
                            )
                            SettingsDivider()
                            SettingsOptionItem(
                                icon = Icons.Default.VisibilityOff,
                                title = "Gizli Hesap",
                                subtitle = "Hesabınız gizli olduğunda sadece takipçileriniz içeriklerinizi görebilir.",
                                trailingContent = {
                                    Switch(
                                        checked = isAccountPrivate,
                                        onCheckedChange = null, // Çift tetiklenmeyi önlemek için tıklamayı ana satıra veriyoruz
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = customColors.surface,
                                            checkedTrackColor = customColors.navy,

                                            uncheckedThumbColor = customColors.surface,
                                            uncheckedTrackColor = customColors.borderStrong,
                                            uncheckedBorderColor = customColors.borderStrong
                                        )
                                    )
                                },

                                onClick = {
                                    isAccountPrivate = !isAccountPrivate
                                }
                            )
                        }
                    }
                }
                item {
                    SettingsSectionHeader(title = "Beslenme & Alerjenler")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            SettingsOptionItem(
                                icon = Icons.Default.Tune,
                                title = "Beslenme & Alerjen Tercihleri",
                                subtitle = "AI önerileri, diyabet, alerji ve beslenme kısıtlamalarınızı yönetin.",
                                onClick = {
                                     navigator.push(EditHealthScreen())
                                }
                            )
                        }
                    }
                }

                // 2. Uygulama Tercihleri
                item {
                    SettingsSectionHeader(title = "Uygulama Tercihleri")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            SettingsOptionItem(
                                icon = Icons.Default.Notifications,
                                title = "Bildirim Ayarları",
                                trailingContent = {
                                    Switch(
                                        checked = isNotificationsEnabled,
                                        onCheckedChange = { isNotificationsEnabled = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = customColors.surface,
                                            checkedTrackColor = customColors.navy,

                                            uncheckedThumbColor = customColors.surface,
                                            uncheckedTrackColor = customColors.borderStrong,
                                            uncheckedBorderColor = customColors.borderStrong
                                        )
                                    )
                                },
                                onClick = { isNotificationsEnabled = !isNotificationsEnabled }
                            )
                            SettingsDivider()
                            SettingsOptionItem(
                                icon = Icons.Default.DarkMode,
                                title = "Karanlık Mod",
                                trailingContent = {
                                    Switch(
                                        checked = isDarkModeActive,
                                        onCheckedChange = { checked ->
                                            settingsScreenModel.toggleDarkMode(checked)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = customColors.surface,
                                            checkedTrackColor = customColors.navy,

                                            uncheckedThumbColor = customColors.surface,
                                            uncheckedTrackColor = customColors.borderStrong,
                                            uncheckedBorderColor = customColors.borderStrong
                                        )
                                    )
                                },
                                onClick = {
                                    mod = !mod
                                }
                            )
                            SettingsDivider()
                            SettingsOptionItem(
                                icon = Icons.Default.Language,
                                title = "Dil Seçimi",
                                badgeText = "Türkçe",
                                onClick = {
                                    // Dil Seçimi Dialog
                                }
                            )
                        }
                    }
                }

                // 3. Destek & Hakkında
                item {
                    SettingsSectionHeader(title = "Destek & Hakkında")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            SettingsOptionItem(
                                icon = Icons.Default.Policy,
                                title = "Gizlilik Politikası & Kullanım Koşulları",
                                onClick = {
                                    // Webview veya Tarayıcı Yönlendirmesi
                                }
                            )
                            SettingsDivider()
                            SettingsOptionItem(
                                icon = Icons.Default.HelpOutline,
                                title = "Bize Ulaşın / Destek",
                                onClick = {
                                    // Destek ekranı / e-posta tetikleyici
                                }
                            )
                            SettingsDivider()
                            SettingsOptionItem(
                                icon = Icons.Default.Info,
                                title = "Uygulama Sürümü",
                                badgeText = "v1.0.0",
                                showChevron = false,
                                onClick = {}
                            )
                        }
                    }
                }

                // 4. Oturum & Gizlilik
                item {
                    SettingsSectionHeader(title = "Oturum & Gizlilik")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            SettingsOptionItem(
                                icon = Icons.AutoMirrored.Filled.Logout,
                                title = "Çıkış Yap",
                                textColor = customColors.error,
                                iconColor = customColors.error,
                                showChevron = false,
                                onClick = {
                                    showLogoutDialog = true
                                }
                            )
                            SettingsDivider()
                            SettingsOptionItem(
                                icon = Icons.Default.DeleteForever,
                                title = "Hesabımı Sil",
                                textColor = customColors.error,
                                iconColor = customColors.error,
                                showChevron = false,
                                onClick = {
                                    // Hesabı Silme Onay Dialog
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = {
                        if (!settingsState.isActionLoading) showLogoutDialog = false
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = customColors.error,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    title = {
                        Text(
                            text = if (settingsState.isActionLoading) "Lütfen Bekleyin" else "Çıkış Yapılsın mı?",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = customColors.textPrimary
                            )
                        )
                    },
                    text = {
                        if (settingsState.isActionLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = customColors.error,
                                    strokeWidth = 2.5.dp
                                )
                                Text(
                                    text = "Çıkış yapılıyor...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = customColors.textPrimary
                                )
                            }
                        } else {
                            Text(
                                text = "Hesabınızdan çıkış yapmak istediğinize emin misiniz?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = customColors.textSecondary
                            )
                        }
                    },
                    confirmButton = {
                        if (!settingsState.isActionLoading) {
                            Button(
                                onClick = {
                                    settingsScreenModel.logout()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = customColors.error
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Çıkış Yap",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = customColors.surface
                                    )
                                )
                            }
                        }
                    },
                    dismissButton = {
                        if (!settingsState.isActionLoading) {
                            TextButton(onClick = { showLogoutDialog = false }) {
                                Text(text = "Vazgeç", color = customColors.textSecondary)
                            }
                        }
                    },
                    containerColor = customColors.surface,
                    shape = RoundedCornerShape(20.dp)
                )
            }

            if (showChangePasswordSheet) {
                ChangePasswordBottomSheet(
                    isActionLoading = changePasswordState.isLoading,
                    errorMessage = changePasswordState.errorMessage,
                    oldPasswordError = changePasswordState.oldPasswordError,
                    newPasswordError = changePasswordState.newPasswordError,
                    againNewPasswordError = changePasswordState.againNewPasswordError,
                    onDismissRequest = {
                        showChangePasswordSheet = false
                        changePasswordScreenModel.clearMessages()
                    },
                    onClearError = {
                        changePasswordScreenModel.clearMessages()
                    },
                    onSubmitClick = { oldPassword, newPassword, againNew ->
                        changePasswordScreenModel.changePassword(
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
private fun SettingsSectionHeader(title: String) {
    val customColors = LocalCustomColors.current
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            color = customColors.textSecondary,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsDivider() {
    val customColors = LocalCustomColors.current
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = customColors.borderLight
    )
}

@Composable
private fun SettingsOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null, // Alt açıklama metni parametresi
    badgeText: String? = null,
    textColor: Color = Color.Unspecified,
    iconColor: Color? = null,
    showChevron: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val effectiveIconColor = iconColor ?: customColors.textSecondary
    val effectiveTextColor = if (textColor != Color.Unspecified) textColor else customColors.textPrimary

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
            tint = effectiveIconColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))

        // Başlık ve Alt Açıklama Alanı
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = effectiveTextColor
                )
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = customColors.textSecondary
                    )
                )
            }
        }

        if (trailingContent != null) {
            trailingContent()
        } else {
            if (badgeText != null) {
                Surface(
                    color = customColors.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = customColors.textSecondary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (showChevron) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = customColors.textTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
