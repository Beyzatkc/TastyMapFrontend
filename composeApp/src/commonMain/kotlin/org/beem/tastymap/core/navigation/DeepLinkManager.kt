package org.beem.tastymap.core.navigation
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.beem.tastymap.ui.auth.VerifyScreen

object DeepLinkManager {
    private val _navigationEvents = MutableSharedFlow<Screen>(replay = 1, extraBufferCapacity = 1)
    val navigationEvents = _navigationEvents.asSharedFlow()

    var pendingInitialScreen: Screen? = null

    //androdı icin
    fun handleLink(url: String) {
        val uri = url.split("?").getOrNull(1)
        val params = uri?.split("&")?.associate {
            val (key, value) = it.split("=", limit = 2)
            key to value
        }

        val token = params?.get("token")

        if (!token.isNullOrEmpty() && url.contains("/auth/verify")) {
            val screen = VerifyScreen(token)
            pendingInitialScreen = screen
            _navigationEvents.tryEmit(screen)
        }
    }


    /* web iicn
    fun handleLink(url: String) {
        if (url.contains("#verify")) {
            val queryString = url.substringAfter("?", "")
            val params = queryString.split("&").associate {
                val pair = it.split("=")
                pair.getOrElse(0) { "" } to pair.getOrElse(1) { "" }
            }

            val token = params["token"]
            if (!token.isNullOrEmpty()) {
                _navigationEvents.tryEmit(VerifyScreen(token))
            }
        }
    }
    // DeepLinkManager.kt

     */

    fun clear() {
        _navigationEvents.resetReplayCache()
    }
}