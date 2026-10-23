package org.beem.tastymap.ui.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable


fun NavGraphBuilder.searchGraph(navController: NavController) {
    composable<SearchRoute> {
        SearchScreen(navController)
    }
}