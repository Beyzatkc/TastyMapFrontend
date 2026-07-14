package org.beem.tastymap.core.navigation
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.beem.tastymap.ui.auth.forgotPassword.ResetScreen
import org.beem.tastymap.ui.auth.verification.email.VerifyScreen

object DeepLinkManager {
    private val _navigationEvents = Channel<Screen>(Channel.BUFFERED)
    val navigationEvents = _navigationEvents.receiveAsFlow()

    var pendingInitialScreen: Screen? = null



    fun handleLink(url: String) {
        try {
            val parts = url.split("?")
            val basePath = parts.getOrNull(0) ?: ""
            val queryString = parts.getOrNull(1)

            val token = queryString
                ?.split("&")
                ?.map { it.split("=") }
                ?.firstOrNull { it.size == 2 && it[0] == "token" }
                ?.getOrNull(1)

            if (token.isNullOrEmpty()) return

            when {
                basePath.endsWith("/auth/verify") -> {
                    val screen = VerifyScreen(token)
                    pendingInitialScreen = screen
                    _navigationEvents.trySend(screen)
                    println("DEEPLINK_MGR: Email doğrulama ekranına yönlendiriliyor. Token: $token")
                }

                basePath.endsWith("/auth/resetPassword/validate") -> {
                    val screen = ResetScreen(token)
                    pendingInitialScreen = screen
                    _navigationEvents.trySend(screen)
                    println("DEEPLINK_MGR: Şifre sıfırlama ekranına yönlendiriliyor. Token: $token")
                }
            }
        } catch (e: Exception) {
            println("DEEPLINK_MGR: Error parsing url: $url -> ${e.message}")
        }
    }



    /*
    fun handleLink(url: String) {
        when {
            url.contains("#verify") -> {
                println("E-posta doğrulama linki yakalandı")
                val token = extractToken(url)
                if (!token.isNullOrEmpty()) {
                    val screen = VerifyScreen(token)
                    pendingInitialScreen = screen
                    _navigationEvents.trySend(screen)
                }
            }
            url.contains("#reset") -> {
                println("Şifre sıfırlama linki yakalandı")
                val token = extractToken(url)
                if (!token.isNullOrEmpty()) {
                    val screen = ResetScreen(token)
                    pendingInitialScreen = screen
                    _navigationEvents.trySend(screen)
                }
            }
        }
    }

     */

    private fun extractToken(url: String): String? {
        val queryString = url.substringAfter("?", "")
        val params = queryString.split("&").associate {
            val pair = it.split("=")
            pair.getOrElse(0) { "" } to pair.getOrElse(1) { "" }
        }
        return params["token"]
    }

    fun clear() {
        pendingInitialScreen = null
    }

}