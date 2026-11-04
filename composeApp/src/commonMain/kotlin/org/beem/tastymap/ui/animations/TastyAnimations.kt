package org.beem.tastymap.ui.animations
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset

object TastyAnimations {
    private const val DURATION = 400

    fun slideInForward() = (slideInHorizontally(animationSpec = tween(DURATION)) { it } + fadeIn(tween(DURATION))) togetherWith
            (slideOutHorizontally(animationSpec = tween(DURATION)) { -it } + fadeOut(tween(DURATION)))

    fun slideInBackward() = (slideInHorizontally(animationSpec = tween(DURATION)) { -it } + fadeIn(tween(DURATION))) togetherWith
            (slideOutHorizontally(animationSpec = tween(DURATION)) { it } + fadeOut(tween(DURATION)))

    fun scaleFade() = (fadeIn(animationSpec = tween(DURATION)) + scaleIn(initialScale = 0.92f, animationSpec = tween(DURATION))) togetherWith
            fadeOut(animationSpec = tween(200))

    fun slideInUp() = (slideInVertically(animationSpec = tween(DURATION)) { it } + fadeIn(tween(DURATION))) togetherWith
            (slideOutVertically(animationSpec = tween(DURATION)) { -it } + fadeOut(tween(DURATION)))
}