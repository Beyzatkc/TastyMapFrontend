package org.beem.tastymap.core.navigation

import androidx.navigation.NavController
import org.beem.tastymap.ui.auth.ChangeSuccessRoute
import org.beem.tastymap.ui.auth.LogRegRoute
import org.beem.tastymap.ui.auth.VerificationSuccessRoute


class WebVerifyNavigator() : VerifyNavigator {
    override suspend fun verifyEmailOnSuccess(navController: NavController) {
        navController.navigate(VerificationSuccessRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    override suspend fun verifyEmailNavigationTwo(navController: NavController) {
        navController.navigate(LogRegRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    override suspend fun changePasswordOnSuccess(navController: NavController) {
        navController.navigate(LogRegRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    override suspend fun changePasswordOnSuccessTwo(navController: NavController) {
        navController.navigate(ChangeSuccessRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }
}