package org.beem.tastymap.ui.bottomnav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.beem.tastymap.core.local.SettingsManager
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily
import org.koin.compose.koinInject

val LocalBottomBarVisibility = compositionLocalOf<MutableState<Boolean>> { error("BottomBarVisibility not provided") }

class MainScreen : Screen {

    @Composable
    override fun Content() {

        val customColors = LocalCustomColors.current
        val isBottomBarVisible = remember { mutableStateOf(true) }
        val settingsManager: SettingsManager = koinInject()
        val languageCode by settingsManager.languageCode.collectAsState()

        CompositionLocalProvider(LocalBottomBarVisibility provides isBottomBarVisible) {
            TabNavigator(ProfileTab) {

                Scaffold(
                    bottomBar = {
                        // 2. ÖNEMLİ: Eğer görünürlük true ise alt barı çiz, değilse gizle!
                        if (isBottomBarVisible.value) {
                            Column {

                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = customColors.borderLight
                                )

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(65.dp),
                                    color = customColors.surface,
                                    tonalElevation = 0.dp
                                ) {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {

                                        BottomTabItem(
                                            tab = MapTab,
                                            modifier = Modifier.weight(1f)
                                        )

                                        BottomTabItem(
                                            tab = SearchTab,
                                            modifier = Modifier.weight(1f)
                                        )

                                        AITabItem(
                                            tab = AITab,
                                            modifier = Modifier.weight(1f)
                                        )

                                        BottomTabItem(
                                            tab = UploadTab,
                                            modifier = Modifier.weight(1f)
                                        )

                                        BottomTabItem(
                                            tab = ProfileTab,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        CurrentTab()
                    }
                }
            }
        }
    }
}
@Composable
private fun BottomTabItem(
    tab: Tab,
    modifier: Modifier = Modifier
) {
    val tabNavigator = LocalTabNavigator.current
    val customColors = LocalCustomColors.current
    val fontFamily = getAppFontFamily()
    val isSelected = tabNavigator.current == tab

    val color =
        if (isSelected)
            customColors.navy
        else
            customColors.textTertiary

    val textFont =
        if (isSelected)
            FontWeight.ExtraBold
        else
            FontWeight.Normal

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable {
                tabNavigator.current = tab
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Dikeyde ortalandı
    ) {

        tab.options.icon?.let { iconPainter ->
            Icon(
                painter = iconPainter,
                contentDescription = tab.options.title,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = tab.options.title,
            color = color,
            fontSize = if (isSelected) 11.sp else 10.sp,
            fontFamily = fontFamily,
            fontWeight = textFont
        )
    }
}

@Composable
private fun AITabItem(
    tab: Tab,
    modifier: Modifier = Modifier
) {
    val tabNavigator = LocalTabNavigator.current
    val customColors = LocalCustomColors.current
    val isSelected = tabNavigator.current == tab

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable {
                tabNavigator.current = tab
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Diğerleriyle aynı hizaya getirildi
    ) {
        // İsteğe göre kutuyu küçülttük veya tamamen metin/ikon dengesine çektik
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    if (isSelected)
                        customColors.navy
                    else
                        customColors.gourmetOrange
                )
                .padding(horizontal = 10.dp, vertical = 6.dp) // Daha kompakt ve şık rozet görünümü
        ) {


                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text ="AI",
                    color =  Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        Spacer(modifier = Modifier.height(5.dp))


    }
}