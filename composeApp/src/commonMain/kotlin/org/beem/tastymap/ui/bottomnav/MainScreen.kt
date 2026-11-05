package org.beem.tastymap.ui.bottomnav

import AIGraphRoute
import AI
import MapGraphRoute
import map
import ProfileGraphRoute
import my
import SearchGraphRoute
import search
import UploadGraphRoute
import upload
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.beem.tastymap.LocalIsWideScreen
import org.beem.tastymap.ui.auth.AuthGraph
import org.beem.tastymap.ui.auth.ChangeSuccessRoute
import org.beem.tastymap.ui.auth.EmailRoute
import org.beem.tastymap.ui.auth.ForgotRoute
import org.beem.tastymap.ui.auth.LogRegRoute
import org.beem.tastymap.ui.auth.LoginPendingRoute
import org.beem.tastymap.ui.auth.ResetRoute
import org.beem.tastymap.ui.auth.VerificationSuccessRoute
import org.beem.tastymap.ui.auth.VerifyRoute
import org.beem.tastymap.ui.common.NotificationBadgeManager
import org.beem.tastymap.ui.profile.NotificationRoute
import org.beem.tastymap.ui.profile.SettingsRoute
import org.beem.tastymap.ui.splash.SplashRoute
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.*

val LocalNavigationBarVisibility = compositionLocalOf<MutableState<Boolean>> {
    error("NavigationBarVisibility not provided")
}

@Composable
fun MainAppShell(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isNavVisible = remember { mutableStateOf(false) }
    val badgeManager = koinInject<NotificationBadgeManager>()
    val viewModel = koinViewModel<MainViewModel>()
    val hasUnread by badgeManager.hasUnreadBadge.collectAsState()


    val isWideScreen = LocalIsWideScreen.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isTopLevelDestination = currentDestination?.hierarchy?.any {
        it.hasRoute(MapGraphRoute::class) ||
                it.hasRoute(SearchGraphRoute::class) ||
                it.hasRoute(AIGraphRoute::class) ||
                it.hasRoute(UploadGraphRoute::class) ||
                it.hasRoute(ProfileGraphRoute::class)
    } == true

    val isAuthOrSplash = currentDestination == null || currentDestination.hierarchy.any { destination ->
        destination.hasRoute(SplashRoute::class) ||
                destination.hasRoute(AuthGraph::class) ||
                destination.hasRoute(LogRegRoute::class) ||
                destination.hasRoute(ForgotRoute::class) ||
                destination.hasRoute(ResetRoute::class) ||
                destination.hasRoute(ChangeSuccessRoute::class) ||
                destination.hasRoute(EmailRoute::class) ||
                destination.hasRoute(VerifyRoute::class) ||
                destination.hasRoute(VerificationSuccessRoute::class) ||
                destination.hasRoute(LoginPendingRoute::class)
    }

    LaunchedEffect(isAuthOrSplash) {
        if (!isAuthOrSplash) {
            viewModel.checkUnreadNotifications()
        }
    }

    val shouldShowNav = when {
        isAuthOrSplash -> false
        isWideScreen -> true
        else -> isTopLevelDestination
    }
    isNavVisible.value = shouldShowNav

    CompositionLocalProvider(
        LocalNavigationBarVisibility provides isNavVisible
    ) {
        if (isWideScreen) {
            Row(modifier = Modifier.fillMaxSize()) {
                if (isNavVisible.value) {
                    AppNavigationRail(
                        rootNavController = navController,
                        bottomNavController = navController,
                        hasUnreadBadge = hasUnread
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        )
                ) {
                    content()
                }
            }
        } else {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                ),
                bottomBar = {
                    if (isNavVisible.value) {
                        AppBottomBar(bottomNavController = navController)
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun AppBottomBar(bottomNavController: NavController) {
    val customColors = LocalCustomColors.current

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isMapSelected = currentDestination?.hierarchy?.any { it.hasRoute(MapGraphRoute::class) } == true
    val isSearchSelected = currentDestination?.hierarchy?.any { it.hasRoute(SearchGraphRoute::class) } == true
    val isAISelected = currentDestination?.hierarchy?.any { it.hasRoute(AIGraphRoute::class) } == true
    val isUploadSelected = currentDestination?.hierarchy?.any { it.hasRoute(UploadGraphRoute::class) } == true
    val isProfileSelected = currentDestination?.hierarchy?.any { it.hasRoute(ProfileGraphRoute::class) } == true

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
                NavTabItem(
                    title = stringResource(Res.string.nav_map),
                    iconFilled = painterResource(Res.drawable.ic_map_fill),
                    iconOutlined = painterResource(Res.drawable.ic_map_outline),
                    graphRoute = MapGraphRoute,
                    isSelected = isMapSelected,
                    navController = bottomNavController,
                    modifier = Modifier.weight(1f)
                )
                NavTabItem(
                    title = stringResource(Res.string.nav_search),
                    iconFilled = painterResource(Res.drawable.ic_search_fill),
                    iconOutlined = painterResource(Res.drawable.ic_search),
                    graphRoute = SearchGraphRoute,
                    isSelected = isSearchSelected,
                    navController = bottomNavController,
                    modifier = Modifier.weight(1f)
                )
                AITabItem(
                    graphRoute = AIGraphRoute,
                    startRoute = AI,
                    isSelected = isAISelected,
                    navController = bottomNavController,
                    modifier = Modifier.weight(1f)
                )
                NavTabItem(
                    title = stringResource(Res.string.nav_upload),
                    iconFilled = painterResource(Res.drawable.ic_plus_fill),
                    iconOutlined = painterResource(Res.drawable.ic_plus),
                    graphRoute = UploadGraphRoute,
                    isSelected = isUploadSelected,
                    navController = bottomNavController,
                    modifier = Modifier.weight(1f)
                )
                NavTabItem(
                    title = stringResource(Res.string.nav_profile),
                    iconFilled = painterResource(Res.drawable.ic_user_fill),
                    iconOutlined = painterResource(Res.drawable.ic_user_outline),
                    graphRoute = ProfileGraphRoute,
                    isSelected = isProfileSelected,
                    navController = bottomNavController,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AppNavigationRail(
    rootNavController: NavController,
    bottomNavController: NavController,
    hasUnreadBadge: Boolean
) {
    val customColors = LocalCustomColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val fontFamily = getAppFontFamily()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isMapSelected = currentDestination?.hierarchy?.any { it.hasRoute(MapGraphRoute::class) } == true
    val isSearchSelected = currentDestination?.hierarchy?.any { it.hasRoute(SearchGraphRoute::class) } == true
    val isAISelected = currentDestination?.hierarchy?.any { it.hasRoute(AIGraphRoute::class) } == true
    val isUploadSelected = currentDestination?.hierarchy?.any { it.hasRoute(UploadGraphRoute::class) } == true
    val isProfileSelected = currentDestination?.hierarchy?.any { it.hasRoute(ProfileGraphRoute::class) } == true

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
                        Icon(
                            painter = painterResource(Res.drawable.firsticon),
                            contentDescription = "App Logo",
                            tint = customColors.blackWhite, // <-- Temaya göre siyah/beyaz olur
                            modifier = Modifier.size(if (isHovered) 45.dp else 42.dp)
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

                Spacer(modifier = Modifier.height(35.dp))

                RailTabItem(
                    title = stringResource(Res.string.nav_map),
                    iconFilled = painterResource(Res.drawable.ic_map_fill),
                    iconOutlined = painterResource(Res.drawable.ic_map_outline),
                    graphRoute = MapGraphRoute,
                    startRoute = map,
                    isSelected = isMapSelected,
                    navController = bottomNavController,
                    isExpanded = isHovered
                )
                RailTabItem(
                    title = stringResource(Res.string.nav_search),
                    iconFilled = painterResource(Res.drawable.ic_search_fill),
                    iconOutlined = painterResource(Res.drawable.ic_search),
                    graphRoute = SearchGraphRoute,
                    startRoute = search,
                    isSelected = isSearchSelected,
                    navController = bottomNavController,
                    isExpanded = isHovered
                )
                RailAITabItem(
                    graphRoute = AIGraphRoute,
                    startRoute = AI,
                    isSelected = isAISelected,
                    navController = bottomNavController,
                    isExpanded = isHovered
                )
                RailTabItem(
                    title = stringResource(Res.string.nav_upload),
                    iconFilled = painterResource(Res.drawable.ic_plus_fill),
                    iconOutlined = painterResource(Res.drawable.ic_plus),
                    graphRoute = UploadGraphRoute,
                    startRoute = upload,
                    isSelected = isUploadSelected,
                    navController = bottomNavController,
                    isExpanded = isHovered
                )
                RailTabItem(
                    title = stringResource(Res.string.nav_profile),
                    iconFilled = painterResource(Res.drawable.ic_user_fill),
                    iconOutlined = painterResource(Res.drawable.ic_user_outline),
                    graphRoute = ProfileGraphRoute,
                    startRoute = my,
                    isSelected = isProfileSelected,
                    navController = bottomNavController,
                    isExpanded = isHovered
                )

                Spacer(modifier = Modifier.weight(1f))

                RailActionItem(
                    title = stringResource(Res.string.my_profile_notification_cd),
                    iconFilled = painterResource(Res.drawable.ic_bell_outline), // Varsa fill, yoksa outline kullan
                    iconOutlined = painterResource(Res.drawable.ic_bell_outline),
                    isExpanded = isHovered,
                    hasBadge = hasUnreadBadge,
                    onClick = {
                        rootNavController.navigate(NotificationRoute) {
                            popUpTo(MapGraphRoute) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )

                RailActionItem(
                    title = stringResource(Res.string.my_profile_settings_cd),
                    iconFilled = painterResource(Res.drawable.ic_menu),
                    iconOutlined = painterResource(Res.drawable.ic_menu),
                    isExpanded = isHovered,
                    hasBadge = false,
                    onClick = {
                        rootNavController.navigate(SettingsRoute) {
                            popUpTo(MapGraphRoute) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
        VerticalDivider(thickness = 0.5.dp, color = customColors.borderLight)
    }
}

fun navigateToBottomTab(
    navController: NavController,
    graphRoute: Any,
    isWideScreen: Boolean
) {
    navController.navigate(graphRoute) {
        if (isWideScreen) {
            launchSingleTop = true
            restoreState = true
        } else {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

@Composable
private fun NavTabItem(
    title: String,
    iconFilled: Painter,
    iconOutlined: Painter,
    graphRoute: Any,
    isSelected: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val iconPainter = if (isSelected) iconFilled else iconOutlined
    val customColors = LocalCustomColors.current
    val fontFamily = getAppFontFamily()

    val iconColor by animateColorAsState(targetValue = customColors.blackWhite, animationSpec = tween(300))
    val backgroundColor by animateColorAsState(targetValue = if (isSelected) customColors.blackWhite.copy(alpha = 0.12f) else Color.Transparent, animationSpec = tween(300))
    val iconSize by animateDpAsState(targetValue = 22.dp, animationSpec = tween(300))
    val textFont = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Column(
        modifier = modifier.fillMaxHeight().clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            if (!isSelected) {
                navigateToBottomTab(navController, graphRoute, isWideScreen = false)
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(backgroundColor).padding(horizontal = 20.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }
        /*
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, color = iconColor, fontSize = 10.sp, fontFamily = fontFamily, fontWeight = textFont)

         */
    }
}

@Composable
private fun AITabItem(
    graphRoute: Any,
    startRoute: Any,
    isSelected: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current

    val bgColor by animateColorAsState(targetValue = if (isSelected) customColors.navy else customColors.gourmetOrange, animationSpec = tween(300))
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1.0f, animationSpec = tween(300))

    Column(
        modifier = modifier.fillMaxHeight().clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            if (!isSelected) {
                navigateToBottomTab(navController, graphRoute, isWideScreen = false)
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale).clip(CircleShape).background(bgColor).padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_sparkles), // Güncellendi
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "AI", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RailActionItem(
    title: String,
    iconFilled: Painter,
    iconOutlined: Painter,
    isExpanded: Boolean,
    hasBadge: Boolean = false,
    onClick: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val fontFamily = getAppFontFamily()
    val iconColor = customColors.blackWhite
    // Ayarlar vs. tıklandığında değişmesini istersen state bağlayabilirsin, şu anlık outline kullanıyoruz
    val iconPainter = iconOutlined

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
                    painter = iconPainter,
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
private fun RailTabItem(
    title: String,
    iconFilled: Painter,
    iconOutlined: Painter,
    graphRoute: Any,
    startRoute: Any,
    isSelected: Boolean,
    navController: NavController,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val fontFamily = getAppFontFamily()

    val iconPainter = if (isSelected) iconFilled else iconOutlined

    val iconColor by animateColorAsState(targetValue = customColors.blackWhite, animationSpec = tween(300))
    val backgroundColor by animateColorAsState(targetValue = if (isSelected) customColors.blackWhite.copy(alpha = 0.12f) else Color.Transparent, animationSpec = tween(300))
    val iconSize by animateDpAsState(targetValue = 26.dp, animationSpec = tween(300))
    val textFont = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isExpanded) Modifier.padding(horizontal = 12.dp) else Modifier)
            .height(56.dp)
            .then(if (isExpanded) Modifier.clip(RoundedCornerShape(12.dp)).background(backgroundColor) else Modifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                navigateToBottomTab(navController, graphRoute,true)
            }
            .then(if (isExpanded) Modifier.padding(horizontal = 12.dp) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
    ) {
        if (isExpanded) {
            Icon(
                painter = iconPainter,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
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
                    painter = iconPainter,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
private fun RailAITabItem(
    graphRoute: Any,
    startRoute: Any,
    isSelected: Boolean,
    navController: NavController,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current

    val bgColor by animateColorAsState(targetValue = if (isSelected) customColors.navy else customColors.gourmetOrange, animationSpec = tween(300))
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1.0f, animationSpec = tween(300))

    Row(
        modifier = modifier.fillMaxWidth().height(56.dp).clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            navigateToBottomTab(navController, graphRoute,true)
        }.padding(horizontal = if (isExpanded) 20.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale).clip(if (isExpanded) RoundedCornerShape(12.dp) else CircleShape).background(bgColor).padding(horizontal = if (isExpanded) 16.dp else 12.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_sparkles), // Güncellendi
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            AnimatedVisibility(visible = isExpanded, enter = fadeIn() + expandHorizontally(), exit = fadeOut() + shrinkHorizontally()) {
                Text(text = "AI", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}