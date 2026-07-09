package org.beem.tastymap.core.navigation
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import org.beem.tastymap.core.constants.AuthConstants
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.auth.verification.VerificationSuccessScreen

class AuthNavigationHandler(
    private val isWeb: Boolean = false,
    private val messenger: PlatformMessenger? = null
) {
    suspend fun onVerificationSuccess(navigator: Navigator) {
        if (isWeb) {
            messenger?.post(AuthConstants.MSG_VERIFICATION_FINISHED)
            delay(2000)
            navigator.replaceAll(VerificationSuccessScreen())
        } else {
            messenger?.post(AuthConstants.MSG_VERIFICATION_FINISHED)
            delay(2000)
            navigator.replaceAll(LogRegScreen())
        }
    }
}