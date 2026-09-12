package org.beem.tastymap.ui.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.beem.tastymap.ui.profile.myprofile.MyProfileScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.nav_map
import tastymap.composeapp.generated.resources.nav_profile
import tastymap.composeapp.generated.resources.nav_search
import tastymap.composeapp.generated.resources.nav_upload

// 1. HARİTA SEKMESİ
object MapTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.nav_map)
            val icon = rememberVectorPainter(Icons.Default.LocationOn)
            return remember { TabOptions(index = 0u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {
        // MapScreen() buraya gelecek
    }
}

// 2. ARAMA SEKMESİ
object SearchTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.nav_search)
            val icon = rememberVectorPainter(Icons.Default.Search)
            return remember { TabOptions(index = 1u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {
        // SearchScreen() buraya gelecek
    }
}

// 3. YÜKLEME SEKMESİ
object UploadTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.nav_upload)
            val icon = rememberVectorPainter(Icons.Default.AddCircle)
            return remember { TabOptions(index = 2u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {
        // UploadScreen() buraya gelecek
    }
}

// 4. PROFİL SEKMESİ
object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.nav_profile)
            val icon = rememberVectorPainter(Icons.Default.Person)
            return remember { TabOptions(index = 3u, title = title, icon = icon) }
        }

    @Composable
    override fun Content() {
        Navigator(MyProfileScreen()) { navigator ->
            val bottomBarVisibility = LocalBottomBarVisibility.current

            // SİHİRLİ DOKUNUŞ: Sayfa her değiştiğinde bu blok tetiklenir
            LaunchedEffect(navigator.lastItem) {
                // Eğer sayfa geçmişinde (stack) sadece 1 sayfa varsa (yani ana Profildeysek) alt barı göster (true).
                // Eğer 1'den fazlaysa (Ayarlar, Cihazlar vb. derinlere indiysek) alt barı gizle (false).
                bottomBarVisibility.value = navigator.items.size == 1
            }

            CurrentScreen()
        }
    }
}
object AITab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            return TabOptions(
                index = 2u,
                title = "AI",
                icon = rememberVectorPainter(Icons.Default.Star)
            )
        }

    @Composable
    override fun Content() {
        // AI ekranın
    }
}