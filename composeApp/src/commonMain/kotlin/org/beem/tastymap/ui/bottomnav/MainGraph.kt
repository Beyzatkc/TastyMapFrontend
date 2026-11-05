package org.beem.tastymap.ui.bottomnav

import AIGraphRoute
import AITabRoute
import MainRoute
import MapGraphRoute
import MapTabRoute
import ProfileGraphRoute
import ProfileTabRoute
import SearchGraphRoute
import SearchTabRoute
import UploadGraphRoute
import UploadTabRoute
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.beem.tastymap.ui.ai.AiChatScreen
import org.beem.tastymap.ui.map.TastyMapScreen
import org.beem.tastymap.ui.post.create.CreatePostScreen
import org.beem.tastymap.ui.profile.myprofile.MyProfileScreen
import org.beem.tastymap.ui.search.SearchScreen

fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation<MainRoute>(
        startDestination = MapGraphRoute // Artık başlangıç noktası Tab değil, Map Graph'ı
    ) {

        // 1. HARİTA SEKMESİ GRAFİĞİ
        navigation<MapGraphRoute>(startDestination = MapTabRoute) {
            composable<MapTabRoute> {
                TastyMapScreen()
            }
        }

        // 2. ARAMA SEKMESİ GRAFİĞİ
        navigation<SearchGraphRoute>(startDestination = SearchTabRoute) {
            composable<SearchTabRoute> {
                SearchScreen(navController)
            }
            // EĞER ARAMA SEKMESİNDE BAŞKASININ PROFİLİNE TIKLANIRSA,
            // Profil detay ekranını bu grafiğe DE eklemelisin!
            // (Shared Navigation mantığı)
        }

        // 3. AI SEKMESİ GRAFİĞİ
        navigation<AIGraphRoute>(startDestination = AITabRoute) {
            composable<AITabRoute> {
                AiChatScreen(navController, false)
            }
        }

        // 4. YÜKLEME SEKMESİ GRAFİĞİ
        navigation<UploadGraphRoute>(startDestination = UploadTabRoute) {
            composable<UploadTabRoute> {
                CreatePostScreen(navController)
            }
        }

        // 5. KENDİ PROFİL SEKMESİ GRAFİĞİ
        navigation<ProfileGraphRoute>(startDestination = ProfileTabRoute) {
            composable<ProfileTabRoute> {
                MyProfileScreen(navController = navController, showBackButton = false)
            }
            // Ayarlar, Bildirimler gibi kendi profiline ait sayfalar buraya eklenebilir.
        }
    }
}