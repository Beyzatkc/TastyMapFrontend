package org.beem.tastymap.ui.bottomnav

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.beem.tastymap.ui.common.NotificationBadgeManager
import org.beem.tastymap.ui.profile.myprofile.notification.NotificationScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily
import org.beem.tastymap.ui.profile.myprofile.settings.SettingsScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.app_logo
import tastymap.composeapp.generated.resources.my_profile_notification_cd
import tastymap.composeapp.generated.resources.my_profile_settings_cd

val LocalNavigationBarVisibility = compositionLocalOf<MutableState<Boolean>> { error("NavigationBarVisibility not provided") }
val LocalIsWideScreen = compositionLocalOf { false }

class MainScreen : Screen {
    @Composable
    override fun Content() {
        val isNavVisible = remember { mutableStateOf(true) }
        val badgeManager = koinInject<NotificationBadgeManager>()
        val mainScreenModel = koinScreenModel<MainScreenModel>()
        val hasUnread by badgeManager.hasUnreadBadge.collectAsState()
        val rootNavigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            mainScreenModel.checkUnreadNotifications()
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 600.dp

            CompositionLocalProvider(
                LocalNavigationBarVisibility provides isNavVisible,
                LocalIsWideScreen provides isWideScreen
            ) {
                TabNavigator(ProfileTab) {
                    if (isWideScreen) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            if (isNavVisible.value) {
                                AppNavigationRail(rootNavigator = rootNavigator, hasUnreadBadge = hasUnread)
                            }
                            ScaffoldContent(
                                modifier = Modifier.weight(1f),
                                contentWindowInsets = WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                                )
                            )
                        }
                    } else {
                        Scaffold(
                            contentWindowInsets = WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                            ),
                            bottomBar = {
                                if (isNavVisible.value) {
                                    AppBottomBar()
                                }
                            }
                        ) { innerPadding ->
                            ScaffoldContent(modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBottomBar() {
    val customColors = LocalCustomColors.current

    Column {
        HorizontalDivider(thickness = 0.5.dp, color = customColors.borderLight)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .windowInsetsPadding(WindowInsets.navigationBars),
            color = customColors.surface,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                NavTabItem(tab = MapTab, modifier = Modifier.weight(1f))
                NavTabItem(tab = SearchTab, modifier = Modifier.weight(1f))
                AITabItem(tab = AITab, modifier = Modifier.weight(1f))
                NavTabItem(tab = UploadTab, modifier = Modifier.weight(1f))
                NavTabItem(tab = ProfileTab, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun NavTabItem(tab: Tab, modifier: Modifier = Modifier) {
    val tabNavigator = LocalTabNavigator.current
    val customColors = LocalCustomColors.current
    val fontFamily = getAppFontFamily()
    val isSelected = tabNavigator.current == tab

    val iconColor by animateColorAsState(targetValue = customColors.blackWhite, animationSpec = tween(300))
    val backgroundColor by animateColorAsState(targetValue = if (isSelected) customColors.blackWhite.copy(alpha = 0.12f) else Color.Transparent, animationSpec = tween(300))
    val iconSize by animateDpAsState(targetValue = 22.dp, animationSpec = tween(300))
    val textFont = if (isSelected) FontWeight.Bold else FontWeight.Medium

    // Seçiliyse dolu (Filled), değilse çizgisel (Outlined) ikon seçimi
    val iconVector = when (tab) {
        MapTab -> if (isSelected) Icons.Default.LocationOn else Icons.Outlined.LocationOn
        SearchTab -> if (isSelected) Icons.Default.Search else Icons.Outlined.Search
        UploadTab -> if (isSelected) Icons.Default.AddCircle else Icons.Outlined.AddCircleOutline
        ProfileTab -> if (isSelected) Icons.Default.Person else Icons.Outlined.Person
        else -> Icons.Default.LocationOn
    }

    Column(
        modifier = modifier.fillMaxHeight().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { tabNavigator.current = tab },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(backgroundColor).padding(horizontal = 20.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = tab.options.title,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = tab.options.title, color = iconColor, fontSize = 10.sp, fontFamily = fontFamily, fontWeight = textFont)
    }
}

@Composable
private fun AITabItem(tab: Tab, modifier: Modifier = Modifier) {
    val tabNavigator = LocalTabNavigator.current
    val customColors = LocalCustomColors.current
    val isSelected = tabNavigator.current == tab

    val bgColor by animateColorAsState(targetValue = if (isSelected) customColors.navy else customColors.gourmetOrange, animationSpec = tween(300))
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1.0f, animationSpec = tween(300))

    Column(
        modifier = modifier.fillMaxHeight().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { tabNavigator.current = tab },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale).clip(CircleShape).background(bgColor).padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(imageVector = Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "AI", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AppNavigationRail(
    rootNavigator: Navigator,
    hasUnreadBadge: Boolean
) {
    val customColors = LocalCustomColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val fontFamily = getAppFontFamily()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val railWidth by animateDpAsState(
        targetValue = if (isHovered) 220.dp else 80.dp,
        animationSpec = tween(250), label = "RailWidthAnimation"
    )

    Row(modifier = Modifier.hoverable(interactionSource)) {
        Surface(
            modifier = Modifier.fillMaxHeight().width(railWidth),
            color = customColors.surface,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = if (isHovered) Alignment.Start else Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isHovered) 16.dp else 0.dp),
                    contentAlignment = if (isHovered) Alignment.CenterStart else Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = if (isHovered) 0.dp else 12.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(if (isHovered) 42.dp else 52.dp)
                        )
                        AnimatedVisibility(
                            visible = isHovered,
                            enter = fadeIn(tween(200)) + expandHorizontally(),
                            exit = fadeOut(tween(150)) + shrinkHorizontally()
                        ) {
                            Text(
                                text = "TastyMap",
                                color = customColors.blackWhite,
                                fontSize = 18.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Menü Öğeleri
                RailTabItem(tab = MapTab, isExpanded = isHovered)
                RailTabItem(tab = SearchTab, isExpanded = isHovered)
                RailAITabItem(tab = AITab, isExpanded = isHovered)
                RailTabItem(tab = UploadTab, isExpanded = isHovered)
                RailTabItem(tab = ProfileTab, isExpanded = isHovered)

                Spacer(modifier = Modifier.weight(1f))

                RailActionItem(
                    title = stringResource(Res.string.my_profile_notification_cd),
                    icon =  Icons.Outlined.Notifications,
                    isExpanded = isHovered,
                    hasBadge = hasUnreadBadge,
                    onClick = { rootNavigator.push(NotificationScreen()) }
                )

                RailActionItem(
                    title = stringResource(Res.string.my_profile_settings_cd),
                    icon = Icons.Default.Menu,
                    isExpanded = isHovered,
                    hasBadge = false,
                    onClick = { rootNavigator.push(SettingsScreen()) }
                )
            }
        }
        VerticalDivider(thickness = 0.5.dp, color = customColors.borderLight)
    }
}

@Composable
private fun RailActionItem(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    hasBadge: Boolean = false,
    onClick: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val fontFamily = getAppFontFamily()
    val iconColor = customColors.blackWhite

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = if (isExpanded) 12.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
    ) {
        Box(
            modifier = Modifier.padding(if (isExpanded) 12.dp else 14.dp),
            contentAlignment = Alignment.Center
        ) {
            BadgedBox(
                badge = {
                    if (hasBadge) {
                        Badge(containerColor = Color.Red, modifier = Modifier.size(8.dp))
                    }
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(tween(200)) + expandHorizontally(),
            exit = fadeOut(tween(150)) + shrinkHorizontally()
        ) {
            Text(
                text = title,
                color = iconColor,
                fontSize = 15.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RailTabItem(tab: Tab, isExpanded: Boolean, modifier: Modifier = Modifier) {
    val tabNavigator = LocalTabNavigator.current
    val customColors = LocalCustomColors.current
    val fontFamily = getAppFontFamily()
    val isSelected = tabNavigator.current == tab

    val iconColor by animateColorAsState(targetValue = customColors.blackWhite, animationSpec = tween(300))
    val backgroundColor by animateColorAsState(targetValue = if (isSelected) customColors.blackWhite.copy(alpha = 0.12f) else Color.Transparent, animationSpec = tween(300))
    val iconSize by animateDpAsState(targetValue = 26.dp, animationSpec = tween(300))
    val textFont = if (isSelected) FontWeight.Bold else FontWeight.Medium

    // Seçiliyse dolu (Filled), değilse çizgisel (Outlined) ikon seçimi
    val iconVector = when (tab) {
        MapTab -> if (isSelected) Icons.Default.LocationOn else Icons.Outlined.LocationOn
        SearchTab -> if (isSelected) Icons.Default.Search else Icons.Outlined.Search
        UploadTab -> if (isSelected) Icons.Default.AddCircle else Icons.Outlined.AddCircleOutline
        ProfileTab -> if (isSelected) Icons.Default.Person else Icons.Outlined.Person
        else -> Icons.Default.LocationOn
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isExpanded) Modifier.padding(horizontal = 12.dp) else Modifier)
            .height(56.dp)
            .then(if (isExpanded) Modifier.clip(RoundedCornerShape(12.dp)).background(backgroundColor) else Modifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { tabNavigator.current = tab }
            .then(if (isExpanded) Modifier.padding(horizontal = 12.dp) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
    ) {
        if (isExpanded) {
            Icon(
                imageVector = iconVector,
                contentDescription = tab.options.title,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = tab.options.title,
                color = iconColor,
                fontSize = 15.sp,
                fontFamily = fontFamily,
                fontWeight = textFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = tab.options.title,
                    tint = iconColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
private fun RailAITabItem(tab: Tab, isExpanded: Boolean, modifier: Modifier = Modifier) {
    val tabNavigator = LocalTabNavigator.current
    val customColors = LocalCustomColors.current
    val isSelected = tabNavigator.current == tab

    val bgColor by animateColorAsState(targetValue = if (isSelected) customColors.navy else customColors.gourmetOrange, animationSpec = tween(300))
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1.0f, animationSpec = tween(300))

    Row(
        modifier = modifier.fillMaxWidth().height(56.dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { tabNavigator.current = tab }.padding(horizontal = if (isExpanded) 20.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale).clip(if (isExpanded) RoundedCornerShape(12.dp) else CircleShape).background(bgColor).padding(horizontal = if (isExpanded) 16.dp else 12.dp, vertical = 12.dp)
        ) {
            Icon(imageVector = Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            AnimatedVisibility(visible = isExpanded, enter = fadeIn() + expandHorizontally(), exit = fadeOut() + shrinkHorizontally()) {
                Text(text = "AI", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun ScaffoldContent(modifier: Modifier = Modifier, contentWindowInsets: WindowInsets = WindowInsets(0.dp)) {
    Box(modifier = modifier.fillMaxSize().windowInsetsPadding(contentWindowInsets)) {
        val tabNavigator = LocalTabNavigator.current
        Crossfade(targetState = tabNavigator.current, animationSpec = tween(durationMillis = 250), label = "TabCrossfadeTransition") { targetTab ->
            targetTab.Content()
        }
    }
}