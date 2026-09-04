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


    /*
    fun handleLink(url: String) {
        try {
            // 1. ADIM: Fonksiyona URL gerçekten ulaştı mı?
            println("DEEPLINK_MGR: Gelen Ham URL -> $url")

            val parts = url.split("?")
            val basePath = parts.getOrNull(0) ?: ""
            val queryString = parts.getOrNull(1)

            println("DEEPLINK_MGR: BasePath -> $basePath")
            println("DEEPLINK_MGR: QueryString -> $queryString")

            val token = queryString
                ?.split("&")
                ?.map { it.split("=") }
                ?.firstOrNull { it.size >= 2 && it[0] == "token" }
                ?.getOrNull(1)

            println("DEEPLINK_MGR: Ayrıştırılan Token -> $token")

            if (token.isNullOrEmpty()) {
                println("DEEPLINK_MGR: HATA - Token bulunamadı veya boş!")
                return
            }

            // Katı 'endsWith' yerine daha esnek olan 'contains' kontrolü
            when {
                basePath.contains("/auth/verify") -> {
                    val screen = VerifyScreen(token)
                    pendingInitialScreen = screen
                    val result = _navigationEvents.trySend(screen)
                    println("DEEPLINK_MGR: VerifyScreen Event Gönderildi mi? -> ${result.isSuccess}")
                }

                basePath.contains("/auth/resetPassword/validate") -> {
                    val screen = ResetScreen(token)
                    pendingInitialScreen = screen
                    val result = _navigationEvents.trySend(screen)
                    println("DEEPLINK_MGR: ResetScreen Event Gönderildi mi? -> ${result.isSuccess}")
                }
                else -> {
                    println("DEEPLINK_MGR: HATA - Path eşleşmedi! BasePath: $basePath")
                }
            }
        } catch (e: Exception) {
            println("DEEPLINK_MGR: Error parsing url: $url -> ${e.message}")
        }
    }

     */




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