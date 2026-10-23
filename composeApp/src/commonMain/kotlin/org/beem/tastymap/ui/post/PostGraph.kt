package org.beem.tastymap.ui.post

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import org.beem.tastymap.ui.auth.AuthGraph
import org.beem.tastymap.ui.auth.ChangeSuccessRoute
import org.beem.tastymap.ui.auth.EmailRoute
import org.beem.tastymap.ui.auth.ForgotRoute
import org.beem.tastymap.ui.auth.LogRegRoute
import org.beem.tastymap.ui.auth.LoginPendingRoute
import org.beem.tastymap.ui.auth.ResetRoute
import org.beem.tastymap.ui.auth.VerificationSuccessRoute
import org.beem.tastymap.ui.auth.VerifyRoute
import org.beem.tastymap.ui.auth.forgotPassword.ChangeSuccessScreen
import org.beem.tastymap.ui.auth.forgotPassword.ForgotScreen
import org.beem.tastymap.ui.auth.forgotPassword.ResetScreen
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.auth.verification.email.EmailVerificationScreen
import org.beem.tastymap.ui.auth.verification.email.VerificationSuccessScreen
import org.beem.tastymap.ui.auth.verification.email.VerifyScreen
import org.beem.tastymap.ui.auth.verification.loginPending.PendingScreen
import org.beem.tastymap.ui.post.create.CreatePostScreen
import org.beem.tastymap.ui.post.detail.PostDetailScreen
import org.beem.tastymap.ui.post.mypost.updatepost.EditPostScreen
fun NavGraphBuilder.postGraph(navController: NavController) {

        composable<EditPostRoute> {backStackEntry->
            val route = backStackEntry.toRoute<EditPostRoute>()
            EditPostScreen(
                postId = route.postId,
                navController = navController
            )
        }

        composable<PostDetailRoute> {backStackEntry->
            val route = backStackEntry.toRoute<PostDetailRoute>()
            PostDetailScreen(
                postId = route.postId,
                navController = navController
            )
        }
        composable<CreatePostRoute> {
            CreatePostScreen(navController)
        }

}