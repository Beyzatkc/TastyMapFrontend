package org.beem.tastymap.ui.bottomnav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.mainGraph(navController: NavController) {

    composable<MainRoute> {
        MainScreen(rootNavController = navController)
    }
}