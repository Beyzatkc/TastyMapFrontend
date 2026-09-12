package org.beem.tastymap.ui.profile.myprofile.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Devices
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
import org.beem.tastymap.ui.profile.myprofile.settings.blockedusers.BlockedUsersScreen
import org.beem.tastymap.ui.profile.myprofile.settings.changepassword.ChangePasswordBottomSheet
import org.beem.tastymap.ui.profile.myprofile.settings.changepassword.ChangePasswordScreenModel
import org.beem.tastymap.ui.profile.myprofile.settings.edithealth.EditHealthScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.settings_active_devices
import tastymap.composeapp.generated.resources.settings_app_version
import tastymap.composeapp.generated.resources.settings_back_cd
import tastymap.composeapp.generated.resources.settings_blocked_users
import tastymap.composeapp.generated.resources.settings_cancel
import tastymap.composeapp.generated.resources.settings_change_password
import tastymap.composeapp.generated.resources.settings_contact_us
import tastymap.composeapp.generated.resources.settings_dark_mode
import tastymap.composeapp.generated.resources.settings_delete_account
import tastymap.composeapp.generated.resources.settings_language
import tastymap.composeapp.generated.resources.settings_language_default
import tastymap.composeapp.generated.resources.settings_logout
import tastymap.composeapp.generated.resources.settings_logout_dialog_loading_text
import tastymap.composeapp.generated.resources.settings_logout_dialog_loading_title
import tastymap.composeapp.generated.resources.settings_logout_dialog_message
import tastymap.composeapp.generated.resources.settings_logout_dialog_title
import tastymap.composeapp.generated.resources.settings_notifications
import tastymap.composeapp.generated.resources.settings_nutrition_preferences
import tastymap.composeapp.generated.resources.settings_nutrition_preferences_sub
import tastymap.composeapp.generated.resources.settings_privacy_policy
import tastymap.composeapp.generated.resources.settings_private_account
import tastymap.composeapp.generated.resources.settings_private_account_sub
import tastymap.composeapp.generated.resources.settings_section_account_security
import tastymap.composeapp.generated.resources.settings_section_app_preferences
import tastymap.composeapp.generated.resources.settings_section_nutrition
import tastymap.composeapp.generated.resources.settings_section_session_privacy
import tastymap.composeapp.generated.resources.settings_section_support_about
import tastymap.composeapp.generated.resources.settings_title
import kotlin.time.Clock


class SettingsScreen(val isPrivate: Boolean) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val changePasswordScreenModel = koinScreenModel<ChangePasswordScreenModel>()
        val changePasswordState by changePasswordScreenModel.uiState.collectAsState()

        val settingsScreenModel = koinScreenModel<SettingsScreenModel>()
        val settingsState by settingsScreenModel.uiState.collectAsState()

        val isDarkModePref by settingsScreenModel.isDarkMode.collectAsState()
        val isDarkModeActive = isDarkModePref ?: isSystemInDarkTheme()

        val currentLanguageCode by settingsScreenModel.languageCode.collectAsState()
        var showLanguageDialog by remember { mutableStateOf(false) }

        key(currentLanguageCode) {

            val customColors = LocalCustomColors.current
            val navigator = LocalNavigator.currentOrThrow

            var showChangePasswordSheet by remember { mutableStateOf(false) }
            var showLogoutDialog by remember { mutableStateOf(false) }
            var isNotificationsEnabled by remember { mutableStateOf(true) }

            remember(isPrivate) {
                settingsScreenModel.setInitialPrivacyStatus(isPrivate)
                true
            }

            LaunchedEffect(changePasswordState.successMessageRes) {
                changePasswordState.successMessageRes?.let { res ->
                    showChangePasswordSheet = false
                    ToastManager.show(getString(res))
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
                                text = stringResource(Res.string.settings_title),
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
                                    contentDescription = stringResource(Res.string.settings_back_cd),
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
                if (settingsState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = customColors.gourmetOrange,
                            strokeWidth = 3.dp
                        )
                    }
                } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Hesabım & Güvenlik
                    item {
                        SettingsSectionHeader(title = stringResource(Res.string.settings_section_account_security))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = customColors.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column {
                                SettingsOptionItem(
                                    icon = Icons.Default.Lock,
                                    title = stringResource(Res.string.settings_change_password),
                                    onClick = {
                                        changePasswordScreenModel.clearMessages()
                                        showChangePasswordSheet = true
                                    }
                                )
                                SettingsDivider()
                                SettingsOptionItem(
                                    icon = Icons.Default.Devices,
                                    title = stringResource(Res.string.settings_active_devices),
                                    onClick = {
                                        navigator.push(ActiveDevicesScreen())
                                    }
                                )
                                SettingsDivider()
                                SettingsOptionItem(
                                    icon = Icons.Default.Block,
                                    title = stringResource(Res.string.settings_blocked_users),
                                    onClick = {
                                        navigator.push(BlockedUsersScreen())
                                    }
                                )
                                SettingsDivider()

                                // GİZLİ HESAP (Tıklama mantığı sadece Switch'te)
                                SettingsOptionItem(
                                    icon = Icons.Default.VisibilityOff,
                                    title = stringResource(Res.string.settings_private_account),
                                    subtitle = stringResource(Res.string.settings_private_account_sub),
                                    trailingContent = {
                                        Switch(
                                            checked = settingsState.isAccountPrivate,
                                            enabled = !settingsState.isPrivacyLoading,
                                            onCheckedChange = { isChecked ->
                                                if (!settingsState.isPrivacyLoading) {
                                                    settingsScreenModel.updatePrivacyStatus(
                                                        isChecked
                                                    )
                                                }
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
                                )
                            }
                        }
                    }

                    item {
                        SettingsSectionHeader(title = stringResource(Res.string.settings_section_nutrition))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = customColors.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column {
                                SettingsOptionItem(
                                    icon = Icons.Default.Tune,
                                    title = stringResource(Res.string.settings_nutrition_preferences),
                                    subtitle = stringResource(Res.string.settings_nutrition_preferences_sub),
                                    onClick = {
                                        navigator.push(EditHealthScreen())
                                    }
                                )
                            }
                        }
                    }

                    // 2. Uygulama Tercihleri
                    item {
                        SettingsSectionHeader(title = stringResource(Res.string.settings_section_app_preferences))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = customColors.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column {
                                // BİLDİRİMLER (Tıklama mantığı sadece Switch'te)
                                SettingsOptionItem(
                                    icon = Icons.Default.Notifications,
                                    title = stringResource(Res.string.settings_notifications),
                                    trailingContent = {
                                        Switch(
                                            checked = isNotificationsEnabled,
                                            onCheckedChange = { isChecked ->
                                                isNotificationsEnabled = isChecked
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
                                )
                                SettingsDivider()

                                SettingsOptionItem(
                                    icon = Icons.Default.DarkMode,
                                    title = stringResource(Res.string.settings_dark_mode),
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
                                    }
                                )
                                SettingsDivider()
                                SettingsOptionItem(
                                    icon = Icons.Default.Language,
                                    title = stringResource(Res.string.settings_language),
                                    badgeText = if (currentLanguageCode == "tr") "Türkçe" else "English",
                                    onClick = {
                                        showLanguageDialog = true
                                    }
                                )
                            }
                        }
                    }

                    // 3. Destek & Hakkında
                    item {
                        SettingsSectionHeader(title = stringResource(Res.string.settings_section_support_about))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = customColors.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column {
                                SettingsOptionItem(
                                    icon = Icons.Default.Policy,
                                    title = stringResource(Res.string.settings_privacy_policy),
                                    onClick = { }
                                )
                                SettingsDivider()
                                SettingsOptionItem(
                                    icon = Icons.Default.HelpOutline,
                                    title = stringResource(Res.string.settings_contact_us),
                                    onClick = { }
                                )
                                SettingsDivider()
                                SettingsOptionItem(
                                    icon = Icons.Default.Info,
                                    title = stringResource(Res.string.settings_app_version),
                                    badgeText = "v1.0.0",
                                    showChevron = false
                                )
                            }
                        }
                    }

                    // 4. Oturum & Gizlilik
                    item {
                        SettingsSectionHeader(title = stringResource(Res.string.settings_section_session_privacy))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = customColors.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column {
                                SettingsOptionItem(
                                    icon = Icons.AutoMirrored.Filled.Logout,
                                    title = stringResource(Res.string.settings_logout),
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
                                    title = stringResource(Res.string.settings_delete_account),
                                    textColor = customColors.error,
                                    iconColor = customColors.error,
                                    showChevron = false,
                                    onClick = { }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

                if (showLanguageDialog) {
                    AlertDialog(
                        onDismissRequest = { showLanguageDialog = false },
                        title = {
                            Text(
                                text = stringResource(Res.string.settings_language),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = customColors.textPrimary
                                )
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            settingsScreenModel.setLanguage("tr")
                                            showLanguageDialog = false
                                        }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = currentLanguageCode == "tr",
                                        onClick = {
                                            settingsScreenModel.setLanguage("tr")
                                            showLanguageDialog = false
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Türkçe",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = customColors.textPrimary
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            settingsScreenModel.setLanguage("en")
                                            showLanguageDialog = false
                                        }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = currentLanguageCode == "en",
                                        onClick = {
                                            settingsScreenModel.setLanguage("en")
                                            showLanguageDialog = false
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "English",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = customColors.textPrimary
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showLanguageDialog = false }) {
                                Text(
                                    text = stringResource(Res.string.settings_cancel),
                                    color = customColors.textSecondary
                                )
                            }
                        },
                        containerColor = customColors.surface,
                        shape = RoundedCornerShape(20.dp)
                    )
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
                                text = if (settingsState.isActionLoading) stringResource(Res.string.settings_logout_dialog_loading_title) else stringResource(
                                    Res.string.settings_logout_dialog_title
                                ),
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
                                        text = stringResource(Res.string.settings_logout_dialog_loading_text),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = customColors.textPrimary
                                    )
                                }
                            } else {
                                Text(
                                    text = stringResource(Res.string.settings_logout_dialog_message),
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
                                        text = stringResource(Res.string.settings_logout),
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
                                    Text(
                                        text = stringResource(Res.string.settings_cancel),
                                        color = customColors.textSecondary
                                    )
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
                        oldPasswordError = changePasswordState.oldPasswordError?.let {
                            stringResource(
                                it
                            )
                        },
                        newPasswordError = changePasswordState.newPasswordError?.let {
                            stringResource(
                                it
                            )
                        },
                        againNewPasswordError = changePasswordState.againNewPasswordError?.let {
                            stringResource(
                                it
                            )
                        },
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
    subtitle: String? = null,
    badgeText: String? = null,
    textColor: Color = Color.Unspecified,
    iconColor: Color? = null,
    showChevron: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null // Opsiyonel duruma getirildi
) {
    val customColors = LocalCustomColors.current
    val effectiveIconColor = iconColor ?: customColors.textSecondary
    val effectiveTextColor = if (textColor != Color.Unspecified) textColor else customColors.textPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
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

