package org.beem.tastymap.core.navigation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.beem.tastymap.ui.auth.ResetRoute
import org.beem.tastymap.ui.auth.VerifyRoute
import org.beem.tastymap.ui.auth.forgotPassword.ResetScreen
import org.beem.tastymap.ui.auth.verification.email.VerifyScreen
import org.beem.tastymap.ui.profile.ProfileRoute
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreen

object DeepLinkManager {
    private val _navigationEvents = Channel<Any>(Channel.BUFFERED)
    val navigationEvents = _navigationEvents.receiveAsFlow()

    var pendingInitialRoute: Any? = null


/*
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
                    // YENİ: Screen yerine VerifyRoute nesnesi oluşturuluyor
                    val route = VerifyRoute(token)
                    pendingInitialRoute = route
                    val result = _navigationEvents.trySend(route)
                    println("DEEPLINK_MGR: VerifyRoute Event Gönderildi mi? -> ${result.isSuccess}")
                }

                basePath.contains("/auth/resetPassword/validate") -> {
                    val token = extractToken(url)
                    if (token.isNullOrEmpty()) {
                        println("DEEPLINK_MGR: HATA - Reset için Token bulunamadı!")
                        return
                    }
                    // YENİ: Screen yerine ResetRoute nesnesi oluşturuluyor
                    val route = ResetRoute(token)
                    pendingInitialRoute = route
                    val result = _navigationEvents.trySend(route)
                    println("DEEPLINK_MGR: ResetRoute Event Gönderildi mi? -> ${result.isSuccess}")
                }

                basePath.contains("/profile/") -> {
                    // Profil linkleri için path parametresini güvenle çekiyoruz
                    val userIdStr = extractPathParameter(url, "/profile/")
                    val userId = userIdStr?.toLongOrNull()

                    println("DEEPLINK_MGR: Profil ID yakalandı -> $userId")

                    if (userId != null) {
                        // YENİ: Screen yerine ProfileRoute nesnesi oluşturuluyor
                        val route = ProfileRoute(userId = userId)
                        pendingInitialRoute = route
                        val result = _navigationEvents.trySend(route)
                        println("DEEPLINK_MGR: ProfileRoute Event Gönderildi mi? -> ${result.isSuccess}")
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

 */


    fun handleLink(url: String) {
        when {
            url.contains("#verify") -> {
                println("E-posta doğrulama linki yakalandı")
                val token = extractToken(url)
                if (!token.isNullOrEmpty()) {
                    val route = VerifyRoute(token)
                    pendingInitialRoute = route
                    _navigationEvents.trySend(route)
                }
            }
            url.contains("#reset") -> {
                println("Şifre sıfırlama linki yakalandı")
                val token = extractToken(url)
                if (!token.isNullOrEmpty()) {
                    val route = ResetRoute(token)
                    pendingInitialRoute = route
                    _navigationEvents.trySend(route)
                }
            }

            url.contains("/profile/") -> {
                println("DEEPLINK_MGR: Profil yönlendirmesi yakalandı")
                val userIdStr = extractPathParameter(url, "/profile/")
                val userId = userIdStr?.toLongOrNull()

                if (userId != null) {
                    val route = ProfileRoute(userId = userId,"")
                    pendingInitialRoute = route
                    _navigationEvents.trySend(route)
                }
            }
        }
    }

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
        pendingInitialRoute = null
    }

}