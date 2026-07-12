package org.beem.tastymap.core.navigation
import cafe.adriel.voyager.navigator.Navigator

interface VerifyNavigator {
    suspend fun verifyEmailOnSuccess(navigator: Navigator)
    suspend fun verifyEmailNavigationTwo(navigator: Navigator)
    suspend fun changePasswordOnSuccess(navigator: Navigator)

    suspend fun changePasswordOnSuccessTwo(navigator: Navigator)
}