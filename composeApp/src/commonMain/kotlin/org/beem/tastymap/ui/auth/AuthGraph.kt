package org.beem.tastymap.ui.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import org.beem.tastymap.ui.auth.forgotPassword.ChangeSuccessScreen
import org.beem.tastymap.ui.auth.forgotPassword.ForgotScreen
import org.beem.tastymap.ui.auth.forgotPassword.ResetScreen
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.auth.verification.email.EmailVerificationScreen
import org.beem.tastymap.ui.auth.verification.email.VerificationSuccessScreen
import org.beem.tastymap.ui.auth.verification.email.VerifyScreen
import org.beem.tastymap.ui.auth.verification.loginPending.PendingScreen

fun NavGraphBuilder.authGraph(navController: NavController) {


        composable<LogRegRoute> {
            LogRegScreen(navController)
        }

        composable<ForgotRoute> {
            ForgotScreen(navController)
        }
        composable<ResetRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ResetRoute>()
            ResetScreen(
                token = route.token,
                navController = navController
            )
        }
        composable<ChangeSuccessRoute> {
            ChangeSuccessScreen(navController)
        }
        composable<EmailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<EmailRoute>()


            EmailVerificationScreen(
                email = route.email,
                deviceId = route.deviceId,
                userId = route.userId,
                navController = navController
            )
        }
        composable<VerifyRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<VerifyRoute>()

            VerifyScreen(
                token = route.token,
                navController = navController
            )
        }
        composable<VerificationSuccessRoute> {
            VerificationSuccessScreen(navController)
        }

        composable<LoginPendingRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<LoginPendingRoute>()

            PendingScreen(
                deviceId = route.deviceId,
                navController = navController
            )
        }
}