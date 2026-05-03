package org.beem.tastymap.core.navigation
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.beem.tastymap.ui.auth.VerifyScreen

object DeepLinkManager {
    private val _navigationEvents = MutableSharedFlow<Screen>(replay = 1, extraBufferCapacity = 1)
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun handleLink(url: String) {
        val uri = url.split("?").getOrNull(1)
        val params = uri?.split("&")?.associate {
            val (key, value) = it.split("=")
            key to value
        }

        val token = params?.get("token")

        if (!token.isNullOrEmpty() && url.contains("/auth/verify")) {
            _navigationEvents.tryEmit(VerifyScreen(token))
        }
    }

    fun clear() {
        _navigationEvents.resetReplayCache()
    }
}