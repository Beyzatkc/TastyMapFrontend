package org.beem.tastymap.ui.splash

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable


fun NavGraphBuilder.splashGraph(navController: NavController) {
    composable<SplashRoute> {
        SplashScreen(navController)
    }
}