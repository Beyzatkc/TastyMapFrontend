package org.beem.tastymap.ui.profile.myprofile.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.components.responsiveContentWidth
import org.beem.tastymap.ui.profile.ActiveDevicesRoute
import org.beem.tastymap.ui.profile.BlockedUsersRoute
import org.beem.tastymap.ui.profile.DeleteAccountRoute
import org.beem.tastymap.ui.profile.EditHealthRoute
import org.beem.tastymap.ui.profile.PrivacyPolicyRoute
import org.beem.tastymap.ui.profile.VisitRoute
import org.beem.tastymap.ui.profile.myprofile.settings.changepassword.ChangePasswordBottomSheet
import org.beem.tastymap.ui.profile.myprofile.settings.changepassword.ChangePasswordViewModel

import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val changePasswordViewModel = koinViewModel<ChangePasswordViewModel>()
    val changePasswordState by changePasswordViewModel.uiState.collectAsState()

    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val settingsState by settingsViewModel.uiState.collectAsState()

    val isDarkModePref by settingsViewModel.isDarkMode.collectAsState()
    val isDarkModeActive = isDarkModePref ?: isSystemInDarkTheme()

    val currentLanguageCode by settingsViewModel.languageCode.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }

    key(currentLanguageCode) {
        val customColors = LocalCustomColors.current

        var showChangePasswordSheet by remember { mutableStateOf(false) }
        var showLogoutDialog by remember { mutableStateOf(false) }
        var isNotificationsEnabled by remember { mutableStateOf(true) }

        LaunchedEffect(changePasswordState.successMessageRes) {
            changePasswordState.successMessageRes?.let { res ->
                showChangePasswordSheet = false
                ToastManager.show(res.toString())
                changePasswordViewModel.clearMessages()
            }
        }

        LaunchedEffect(settingsState.isLoggedOut) {
            if (settingsState.isLoggedOut) {
                showLogoutDialog = false
            }
        }

        LaunchedEffect(settingsState.errorMessage) {
            settingsState.errorMessage?.let { message ->
                ToastManager.show(message)
                settingsViewModel.clearMessages()
            }
        }

        Scaffold(
            containerColor = customColors.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.settings_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = customColors.textPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) { // YENİ: popBackStack()
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = customColors.textPrimary,
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.TopCenter
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .responsiveContentWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
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
                                            changePasswordViewModel.clearMessages()
                                            showChangePasswordSheet = true
                                        }
                                    )
                                    SettingsDivider()
                                    SettingsOptionItem(
                                        icon = Icons.Default.Devices,
                                        title = stringResource(Res.string.settings_active_devices),
                                        onClick = {
                                            navController.navigate(ActiveDevicesRoute)
                                        }
                                    )
                                    SettingsDivider()
                                    SettingsOptionItem(
                                        icon = Icons.Default.Block,
                                        title = stringResource(Res.string.settings_blocked_users),
                                        onClick = {
                                            navController.navigate(BlockedUsersRoute)
                                        }
                                    )
                                    SettingsDivider()

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
                                                        settingsViewModel.updatePrivacyStatus(isChecked)
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
                            SettingsSectionHeader(title = stringResource(Res.string.settings_section_activity))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = customColors.surface),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column {
                                    SettingsOptionItem(
                                        icon = Icons.Default.Place,
                                        title = stringResource(Res.string.visit_history_title),
                                        subtitle = stringResource(Res.string.settings_visit_history_sub),
                                        onClick = {
                                            navController.navigate(VisitRoute)
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
                                            navController.navigate(EditHealthRoute)
                                        }
                                    )
                                }
                            }
                        }

                        item {
                            SettingsSectionHeader(title = stringResource(Res.string.settings_section_app_preferences))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = customColors.surface),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column {
                                    SettingsOptionItem(
                                        icon = Icons.Default.Notifications,
                                        title = stringResource(Res.string.settings_notifications),
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
                                                    settingsViewModel.toggleDarkMode(checked)
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
                                        onClick = { showLanguageDialog = true }
                                    )
                                }
                            }
                        }

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
                                        onClick = {
                                            navController.navigate(PrivacyPolicyRoute)
                                        }
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
                                        onClick = { showLogoutDialog = true }
                                    )
                                    SettingsDivider()
                                    SettingsOptionItem(
                                        icon = Icons.Default.DeleteForever,
                                        title = stringResource(Res.string.settings_delete_account),
                                        textColor = customColors.error,
                                        iconColor = customColors.error,
                                        showChevron = false,
                                        onClick = {
                                            navController.navigate(DeleteAccountRoute)
                                        }
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
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
                                        settingsViewModel.setLanguage("tr")
                                        showLanguageDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentLanguageCode == "tr",
                                    onClick = {
                                        settingsViewModel.setLanguage("tr")
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
                                        settingsViewModel.setLanguage("en")
                                        showLanguageDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentLanguageCode == "en",
                                    onClick = {
                                        settingsViewModel.setLanguage("en")
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
                                onClick = { settingsViewModel.logout() },
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
                    oldPasswordError = changePasswordState.oldPasswordError?.let { stringResource(it) },
                    newPasswordError = changePasswordState.newPasswordError?.let { stringResource(it) },
                    againNewPasswordError = changePasswordState.againNewPasswordError?.let { stringResource(it) },
                    onDismissRequest = {
                        showChangePasswordSheet = false
                        changePasswordViewModel.clearMessages()
                    },
                    onClearError = {
                        changePasswordViewModel.clearMessages()
                    },
                    onSubmitClick = { oldPassword, newPassword, againNew ->
                        changePasswordViewModel.changePassword(
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
    subtitle: String? = null,
    badgeText: String? = null,
    textColor: Color = Color.Unspecified,
    iconColor: Color? = null,
    showChevron: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
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