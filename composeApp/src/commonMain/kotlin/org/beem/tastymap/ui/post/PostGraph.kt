package org.beem.tastymap.ui.post

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
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