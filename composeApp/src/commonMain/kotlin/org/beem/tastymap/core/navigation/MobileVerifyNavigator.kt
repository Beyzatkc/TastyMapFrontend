package org.beem.tastymap.core.navigation

import androidx.navigation.NavController
// import cafe.adriel.voyager.navigator.Navigator // <-- Bunu siliyoruz
import kotlinx.coroutines.delay
import org.beem.tastymap.core.constants.AuthConstants
import org.beem.tastymap.ui.auth.LogRegRoute

class MobileVerifyNavigator : VerifyNavigator {

    override suspend fun verifyEmailOnSuccess(navController: NavController) {

        navController.navigate(LogRegRoute) {
            popUpTo(0) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    override suspend fun verifyEmailNavigationTwo(navController: NavController) {

    }

    override suspend fun changePasswordOnSuccess(navController: NavController) {
        navController.navigate(LogRegRoute) {
            popUpTo(0) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    override suspend fun changePasswordOnSuccessTwo(navController: NavController) {
        navController.navigate(LogRegRoute) {
            popUpTo(0) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}