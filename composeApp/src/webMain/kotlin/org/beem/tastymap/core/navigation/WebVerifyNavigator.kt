package org.beem.tastymap.core.navigation

import cafe.adriel.voyager.navigator.Navigator
import org.beem.tastymap.ui.auth.forgotPassword.ChangeSuccessScreen
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.auth.verification.email.VerificationSuccessScreen

class WebVerifyNavigator() : VerifyNavigator {
    override suspend fun verifyEmailOnSuccess(navigator: Navigator) {
        navigator.replaceAll(VerificationSuccessScreen())
    }

    override suspend fun verifyEmailNavigationTwo(navigator: Navigator) {
        navigator.replaceAll(LogRegScreen())
    }

    override suspend fun changePasswordOnSuccess(navigator: Navigator) {
        navigator.replaceAll(LogRegScreen())
    }

    override suspend fun changePasswordOnSuccessTwo(navigator: Navigator) {
        navigator.replaceAll(ChangeSuccessScreen())
    }
}