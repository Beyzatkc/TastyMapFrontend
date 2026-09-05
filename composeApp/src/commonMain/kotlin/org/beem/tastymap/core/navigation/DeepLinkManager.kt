package org.beem.tastymap.core.navigation
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.beem.tastymap.ui.auth.forgotPassword.ResetScreen
import org.beem.tastymap.ui.auth.verification.email.VerifyScreen
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreen

object DeepLinkManager {
    private val _navigationEvents = Channel<Screen>(Channel.BUFFERED)
    val navigationEvents = _navigationEvents.receiveAsFlow()

    var pendingInitialScreen: Screen? = null



    fun handleLink(url: String) {
        try {
            println("DEEPLINK_MGR: Gelen Ham URL -> $url")

            val parts = url.split("?")
            val basePath = parts.getOrNull(0) ?: ""
            val queryString = parts.getOrNull(1)

            println("DEEPLINK_MGR: BasePath -> $basePath")
            println("DEEPLINK_MGR: QueryString -> $queryString")

            // ⚠️ Global token kontrolü kaldırıldı! Artık her link kendi içinde ayrıştırılıyor.

            when {
                basePath.contains("/auth/verify") -> {
                    val token = extractToken(url)
                    if (token.isNullOrEmpty()) {
                        println("DEEPLINK_MGR: HATA - Verify için Token bulunamadı!")
                        return
                    }
                    val screen = VerifyScreen(token)
                    pendingInitialScreen = screen
                    val result = _navigationEvents.trySend(screen)
                    println("DEEPLINK_MGR: VerifyScreen Event Gönderildi mi? -> ${result.isSuccess}")
                }

                basePath.contains("/auth/resetPassword/validate") -> {
                    val token = extractToken(url)
                    if (token.isNullOrEmpty()) {
                        println("DEEPLINK_MGR: HATA - Reset için Token bulunamadı!")
                        return
                    }
                    val screen = ResetScreen(token)
                    pendingInitialScreen = screen
                    val result = _navigationEvents.trySend(screen)
                    println("DEEPLINK_MGR: ResetScreen Event Gönderildi mi? -> ${result.isSuccess}")
                }

                basePath.contains("/profile/") -> {
                    // Profil linkleri için path parametresini güvenle çekiyoruz
                    val userIdStr = extractPathParameter(url, "/profile/")
                    val userId = userIdStr?.toLongOrNull()

                    println("DEEPLINK_MGR: Profil ID yakalandı -> $userId")

                    if (userId != null) {
                        val screen = ProfileScreen(userId = userId)
                        pendingInitialScreen = screen
                        val result = _navigationEvents.trySend(screen)
                        println("DEEPLINK_MGR: ProfileScreen Event Gönderildi mi? -> ${result.isSuccess}")
                    } else {
                        println("DEEPLINK_MGR: HATA - Geçersiz Profil ID!")
                    }
                }

                else -> {
                    println("DEEPLINK_MGR: HATA - Path eşleşmedi! BasePath: $basePath")
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
            url.contains("/profile/") -> {
                println("DEEPLINK_MGR: Profil yönlendirmesi yakalandı")
                val userIdStr = extractPathParameter(url, "/profile/")

                // userId Long olarak bekliyorsan toLongOrNull kullanıyoruz
                val userId = userIdStr?.toLongOrNull()

                if (userId != null) {
                    val screen = ProfileScreen(userId = userId)
                    _navigationEvents.trySend(screen)
                }
            }
        }
    }

 */


    private fun extractPathParameter(url: String, pathPrefix: String): String? {

        return url.substringAfter(pathPrefix, "")
            .substringBefore("?")
            .substringBefore("/")
            .takeIf { it.isNotEmpty() }
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