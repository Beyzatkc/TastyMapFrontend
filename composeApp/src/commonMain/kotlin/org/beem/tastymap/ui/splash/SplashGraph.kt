package org.beem.tastymap.ui.splash

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.beem.tastymap.ui.search.SearchRoute
import org.beem.tastymap.ui.search.SearchScreen


fun NavGraphBuilder.splashGraph(navController: NavController) {
    composable<SplashRoute> {
        SplashScreen(navController)
    }
}