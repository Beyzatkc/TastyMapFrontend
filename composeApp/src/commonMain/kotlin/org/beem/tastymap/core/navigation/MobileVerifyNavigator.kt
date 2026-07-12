package org.beem.tastymap.core.navigation

import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import org.beem.tastymap.core.constants.AuthConstants
import org.beem.tastymap.ui.auth.logReg.LogRegScreen

class MobileVerifyNavigator() : VerifyNavigator {

    override suspend fun verifyEmailOnSuccess(navigator: Navigator) {
        navigator.replaceAll(LogRegScreen())
    }

    override suspend fun verifyEmailNavigationTwo(navigator: Navigator) {

    }

    override suspend fun changePasswordOnSuccess(navigator: Navigator) {
        navigator.replaceAll(LogRegScreen())
    }

    override suspend fun changePasswordOnSuccessTwo(navigator: Navigator) {
        navigator.replaceAll(LogRegScreen())
    }
}