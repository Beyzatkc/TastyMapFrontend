package org.beem.tastymap.ui.profile.myprofile.settings.activedevices

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.theme.LocalCustomColors

class ActiveDevicesScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ActiveDevicesScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val pullToRefreshState = rememberPullToRefreshState()
        val customColors = LocalCustomColors.current

        LaunchedEffect(Unit) {
            screenModel.getActiveDevices()
        }

        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { message ->
                ToastManager.show(message)
            }
        }

        Scaffold(
            containerColor = customColors.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Aktif Cihazlar",
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
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = uiState.isLoading && uiState.devices.isNotEmpty(),
                onRefresh = { screenModel.getActiveDevices() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = uiState.isLoading && uiState.devices.isNotEmpty(),
                        modifier = Modifier.align(Alignment.TopCenter),
                        containerColor = customColors.surface,
                        color = customColors.navy
                    )
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val isError = !uiState.errorMessage.isNullOrBlank()
                    val isEmpty = uiState.devices.isEmpty()

                    when {
                        uiState.isLoading && isEmpty && !isError -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = customColors.navy)
                            }
                        }

                        isError && isEmpty -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                FilledTonalButton(
                                    onClick = { screenModel.getActiveDevices() },
                                    enabled = !uiState.isLoading,
                                    modifier = Modifier.height(48.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = customColors.surfaceVariant,
                                        contentColor = customColors.textPrimary
                                    )
                                ) {
                                    AnimatedContent(
                                        targetState = uiState.isLoading,
                                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                                        label = "ButtonLoadingTransition"
                                    ) { loading ->
                                        if (loading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.5.dp,
                                                color = customColors.textPrimary
                                            )
                                        } else {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "Yeniden Dene",
                                                    modifier = Modifier.size(24.dp),
                                                    tint = customColors.textPrimary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Yeniden Dene",
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = customColors.textPrimary
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = uiState.devices,
                                    key = { it.deviceId }
                                ) { device ->

                                    val deviceIcon: ImageVector = when (device.deviceType) {
                                        DeviceType.ANDROID -> Icons.Default.PhoneAndroid
                                        DeviceType.IOS -> Icons.Default.PhoneIphone
                                        DeviceType.WEB -> Icons.Default.Language
                                        DeviceType.UNKNOWN -> Icons.Default.Android
                                    }

                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = customColors.surface),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = deviceIcon,
                                                contentDescription = null,
                                                tint = customColors.navy,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = device.deviceName,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = customColors.textPrimary
                                                    )
                                                )

                                                Spacer(modifier = Modifier.height(2.dp))

                                                if (!device.location.isNullOrBlank()) {
                                                    Text(
                                                        text = device.location,
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            fontWeight = FontWeight.Normal,
                                                            color = customColors.textSecondary
                                                        )
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                }

                                                Text(
                                                    text = device.formattedLastSeen,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = customColors.textTertiary
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
    }
}