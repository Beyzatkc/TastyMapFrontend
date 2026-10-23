package org.beem.tastymap.core.navigation
import androidx.navigation.NavController

interface VerifyNavigator {
    suspend fun verifyEmailOnSuccess(navController: NavController)
    suspend fun verifyEmailNavigationTwo(navController: NavController)
    suspend fun changePasswordOnSuccess(navController: NavController)

    suspend fun changePasswordOnSuccessTwo(navController: NavController)
}