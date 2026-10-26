package org.beem.tastymap.ui.bottomnav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.beem.tastymap.ui.post.create.CreatePostScreen
import org.beem.tastymap.ui.profile.myprofile.MyProfileScreen
import org.beem.tastymap.ui.search.SearchScreen

fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation<MainRoute>(
        startDestination = MapTabRoute
    ) {
        composable<MapTabRoute> {
            // MapScreen(navController)
        }
        composable<SearchTabRoute> {
            SearchScreen(navController)
        }
        composable<AITabRoute> {
            // AIScreen(navController)
        }
        composable<UploadTabRoute> {
            CreatePostScreen(navController)
        }
        composable<ProfileTabRoute> {
            MyProfileScreen(navController = navController, showBackButton = false)
        }
    }
}