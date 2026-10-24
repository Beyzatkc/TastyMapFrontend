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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import org.beem.tastymap.LocalIsWideScreen
import org.beem.tastymap.core.util.AppConfig.BASE_URL
import org.beem.tastymap.ui.common.NotificationBadgeManager
import org.beem.tastymap.ui.post.create.CreatePostScreen
import org.beem.tastymap.ui.profile.NotificationRoute
import org.beem.tastymap.ui.profile.SettingsRoute
import org.beem.tastymap.ui.profile.myprofile.MyProfileScreen
import org.beem.tastymap.ui.search.SearchScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel // YENİ: KMP ViewModel
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.*

val LocalNavigationBarVisibility = compositionLocalOf<MutableState<Boolean>> { error("NavigationBarVisibility not provided") }


@Composable
fun MainScreen(rootNavController: NavController) {
    val isNavVisible = remember { mutableStateOf(true) }
    val badgeManager = koinInject<NotificationBadgeManager>()
    val viewModel = koinViewModel<MainViewModel>()
    val hasUnread by badgeManager.hasUnreadBadge.collectAsState()

    val bottomNavController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.checkUnreadNotifications()
    }

    // YENİ: BoxWithConstraints silindi! Genişlik bilgisini artık direkt App.kt'den (tepeden) çekiyoruz.
    val isWideScreen = LocalIsWideScreen.current

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isMainScreen = currentDestination?.hierarchy?.any {
        it.hasRoute(MapTabRoute::class) ||
                it.hasRoute(SearchTabRoute::class) ||
                it.hasRoute(AITabRoute::class) ||
                it.hasRoute(UploadTabRoute::class) ||
                it.hasRoute(ProfileTabRoute::class)
    } == true

    LaunchedEffect(currentDestination, isWideScreen) {
        if (isWideScreen) {
            isNavVisible.value = true
        } else {
            isNavVisible.value = isMainScreen
        }
    }

    CompositionLocalProvider(
        LocalNavigationBarVisibility provides isNavVisible
    ) {
        if (isWideScreen) {
            Row(modifier = Modifier.fillMaxSize()) {
                if (isNavVisible.value) {
                    AppNavigationRail(
                        rootNavController = rootNavController,
                        bottomNavController = bottomNavController,
                        hasUnreadBadge = hasUnread
                    )
                }
                ScaffoldContent(
                    rootNavController = rootNavController,
                    bottomNavController = bottomNavController,
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
                        AppBottomBar(bottomNavController = bottomNavController)
                    }
                }
            ) { innerPadding ->
                ScaffoldContent(
                    rootNavController = rootNavController,
                    bottomNavController = bottomNavController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
// 1. ScaffoldContent'e rootNavController parametresini ekliyoruz
@Composable
private fun ScaffoldContent(
    rootNavController: NavController, // YENİ EKLENDİ
    bottomNavController: NavHostController,
    modifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = WindowInsets(0.dp)
) {

    Box(modifier = modifier.fillMaxSize().windowInsetsPadding(contentWindowInsets)) {
        NavHost(
            navController = bottomNavController,
            startDestination = MapTabRoute
        ) {
            composable<MapTabRoute> {
                // MapScreen(bottomNavController)
            }
            composable<SearchTabRoute> {
                SearchScreen(rootNavController)
            }
            composable<AITabRoute> {
                //AIScreen(bottomNavController)
            }
            composable<UploadTabRoute> {
                CreatePostScreen(rootNavController)
            }
            composable<ProfileTabRoute> {
                MyProfileScreen(navController = rootNavController, showBackButton = false)
            }
        }
    }
}

@Composable
private fun AppBottomBar(bottomNavController: NavController) {
    val customColors = LocalCustomColors.current

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

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
                    iconFilled = Icons.Default.LocationOn,
                    iconOutlined = Icons.Outlined.LocationOn,
                    route = MapTabRoute, // .class.qualifiedName sildik, direkt obje verdik!
                    currentDestination = currentDestination,
                    navController = bottomNavController,
                    modifier = Modifier.weight(1f)
                )
                NavTabItem(
                    title = stringResource(Res.string.nav_search),
                    iconFilled = Icons.Default.Search,
                    iconOutlined = Icons.Outlined.Search,
                    route = SearchTabRoute,
                    currentDestination = currentDestination,
                    navController = bottomNavController,
                    modifier = Modifier.weight(1f)
                )
                AITabItem(
                    route = AITabRoute,
                    currentDestination = currentDestination,
                    navController = bottomNavController,
                    modifier = Modifier.weight(1f)
                )
                NavTabItem(
                    title = stringResource(Res.string.nav_upload),
                    iconFilled = Icons.Default.Add,
                    iconOutlined = Icons.Outlined.Add,
                    route = UploadTabRoute,
                    currentDestination = currentDestination,
                    navController = bottomNavController,
                    modifier = Modifier.weight(1f)
                )
                NavTabItem(
                    title = stringResource(Res.string.nav_profile),
                    iconFilled = Icons.Default.Person,
                    iconOutlined = Icons.Outlined.Person,
                    route = ProfileTabRoute,
                    currentDestination = currentDestination,
                    navController = bottomNavController,
                    modifier = Modifier.weight(1f)
                )
            }
            }
    }
}

@Composable
private fun NavTabItem(
    title: String,
    iconFilled: ImageVector,
    iconOutlined: ImageVector,
    route: Any,
    currentDestination: androidx.navigation.NavDestination?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val fontFamily = getAppFontFamily()

    val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true

    val iconColor by animateColorAsState(targetValue = customColors.blackWhite, animationSpec = tween(300))
    val backgroundColor by animateColorAsState(targetValue = if (isSelected) customColors.blackWhite.copy(alpha = 0.12f) else Color.Transparent, animationSpec = tween(300))
    val iconSize by animateDpAsState(targetValue = 22.dp, animationSpec = tween(300))
    val textFont = if (isSelected) FontWeight.Bold else FontWeight.Medium

    val iconVector = if (isSelected) iconFilled else iconOutlined

    Column(
        modifier = modifier.fillMaxHeight().clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().route!!) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
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
                imageVector = iconVector,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, color = iconColor, fontSize = 10.sp, fontFamily = fontFamily, fontWeight = textFont)
    }
}

@Composable
private fun AITabItem(
    route: Any,
    currentDestination: androidx.navigation.NavDestination?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true

    val bgColor by animateColorAsState(targetValue = if (isSelected) customColors.navy else customColors.gourmetOrange, animationSpec = tween(300))
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1.0f, animationSpec = tween(300))

    Column(
        modifier = modifier.fillMaxHeight().clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().route!!) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
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

                RailTabItem(
                    title = stringResource(Res.string.nav_map),
                    iconFilled = Icons.Default.LocationOn,
                    iconOutlined = Icons.Outlined.LocationOn,
                    route = MapTabRoute,
                    currentDestination = currentDestination,
                    navController = bottomNavController,
                    isExpanded = isHovered
                )
                RailTabItem(
                    title = stringResource(Res.string.nav_search),
                    iconFilled = Icons.Default.Search,
                    iconOutlined = Icons.Outlined.Search,
                    route = SearchTabRoute,
                    currentDestination = currentDestination,
                    navController = bottomNavController,
                    isExpanded = isHovered
                )
                RailAITabItem(
                    route = AITabRoute,
                    currentDestination = currentDestination,
                    navController = bottomNavController,
                    isExpanded = isHovered
                )
                RailTabItem(
                    title = stringResource(Res.string.nav_upload),
                    iconFilled = Icons.Default.Add,
                    iconOutlined = Icons.Outlined.Add,
                    route = UploadTabRoute,
                    currentDestination = currentDestination,
                    navController = bottomNavController,
                    isExpanded = isHovered
                )
                RailTabItem(
                    title = stringResource(Res.string.nav_profile),
                    iconFilled = Icons.Default.Person,
                    iconOutlined = Icons.Outlined.Person,
                    route = ProfileTabRoute,
                    currentDestination = currentDestination,
                    navController = bottomNavController,
                    isExpanded = isHovered
                )

                Spacer(modifier = Modifier.weight(1f))

                RailActionItem(
                    title = stringResource(Res.string.my_profile_notification_cd),
                    icon =  Icons.Outlined.Notifications,
                    isExpanded = isHovered,
                    hasBadge = hasUnreadBadge,
                    onClick = { rootNavController.navigate(NotificationRoute) }
                )

                RailActionItem(
                    title = stringResource(Res.string.my_profile_settings_cd),
                    icon = Icons.Default.Menu,
                    isExpanded = isHovered,
                    hasBadge = false,
                    onClick = { rootNavController.navigate(SettingsRoute) }
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
private fun RailTabItem(
    title: String,
    iconFilled: ImageVector,
    iconOutlined: ImageVector,
    route: Any,
    currentDestination: androidx.navigation.NavDestination?,
    navController: NavController,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val fontFamily = getAppFontFamily()

    val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true

    val iconColor by animateColorAsState(targetValue = customColors.blackWhite, animationSpec = tween(300))
    val backgroundColor by animateColorAsState(targetValue = if (isSelected) customColors.blackWhite.copy(alpha = 0.12f) else Color.Transparent, animationSpec = tween(300))
    val iconSize by animateDpAsState(targetValue = 26.dp, animationSpec = tween(300))
    val textFont = if (isSelected) FontWeight.Bold else FontWeight.Medium

    val iconVector = if (isSelected) iconFilled else iconOutlined

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
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().route!!) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            .then(if (isExpanded) Modifier.padding(horizontal = 12.dp) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
    ) {
        if (isExpanded) {
            Icon(
                imageVector = iconVector,
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
                    imageVector = iconVector,
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
    route: Any, // DÜZELTİLDİ: Any yapıldı
    currentDestination: androidx.navigation.NavDestination?,
    navController: NavController,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true

    val bgColor by animateColorAsState(targetValue = if (isSelected) customColors.navy else customColors.gourmetOrange, animationSpec = tween(300))
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1.0f, animationSpec = tween(300))

    Row(
        modifier = modifier.fillMaxWidth().height(56.dp).clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().route!!) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }.padding(horizontal = if (isExpanded) 20.dp else 0.dp),
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