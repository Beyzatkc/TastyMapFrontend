package org.beem.tastymap.core.navigation
import AIGraphRoute
import MapGraphRoute
import ProfileGraphRoute
import SearchGraphRoute
import UploadGraphRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.beem.tastymap.ui.auth.ResetRoute
import org.beem.tastymap.ui.auth.VerifyRoute
import org.beem.tastymap.ui.post.EditPostRoute
import org.beem.tastymap.ui.profile.AccountDeactivatedRoute
import org.beem.tastymap.ui.profile.ActiveDevicesRoute
import org.beem.tastymap.ui.profile.BlockedUsersRoute
import org.beem.tastymap.ui.profile.DeleteAccountRoute
import org.beem.tastymap.ui.profile.EditHealthRoute
import org.beem.tastymap.ui.profile.EditProfileRoute
import org.beem.tastymap.ui.profile.NotificationRoute
import org.beem.tastymap.ui.profile.PrivacyPolicyRoute
import org.beem.tastymap.ui.profile.ProfileRoute
import org.beem.tastymap.ui.profile.SettingsRoute
import org.beem.tastymap.ui.profile.VisitRoute

object DeepLinkManager {
    var initialBrowserUrl: String? = null
    private val _navigationEvents = Channel<Any>(Channel.BUFFERED)
    val navigationEvents = _navigationEvents.receiveAsFlow()

    var pendingInitialRoute: Any? = null
    var pendingRefreshRoute: Any? = null


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
                        val route = ProfileRoute(userId = userId,"")
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




    fun handleWebRefresh(url: String, myUsername: String? = null) {
        val rawHash = url.substringAfter("#", "").substringBefore("?")
        if (rawHash.isEmpty()) return

        val hashLower = rawHash.lowercase()

        if (hashLower.startsWith("verify") || hashLower.startsWith("reset") || hashLower.contains("splash") || hashLower.contains("logreg")) {
            return
        }

        val refreshRoute: Any? = when {
            hashLower.startsWith("editpost/") -> {
                val idString = rawHash.substringAfter("editpost/")
                val parsedId = idString.toLongOrNull()

                if (parsedId != null) {
                    EditPostRoute(postId = parsedId)
                } else null
            }

            hashLower == "map" || hashLower == "maptabroute" || hashLower == "mapgraphroute" || hashLower == "mainroute" -> MapGraphRoute
            hashLower == "search" || hashLower == "searchtabroute" || hashLower == "searchgraphroute" -> SearchGraphRoute
            hashLower == "ai" || hashLower == "aitabroute" || hashLower == "aigraphroute" -> AIGraphRoute
            hashLower == "upload" || hashLower == "uploadtabroute" || hashLower == "uploadgraphroute" -> UploadGraphRoute
            hashLower == "my" || hashLower == "profiletabroute" || hashLower == "profilegraphroute" || hashLower == "myprofilefullscreenroute" -> ProfileGraphRoute

            // Diğer alt ekranlar aynı kalıyor
            hashLower == "notifications" || hashLower == "notificationroute" -> NotificationRoute
            hashLower == "settings" || hashLower == "settingsroute" -> SettingsRoute
            hashLower == "editprofile" || hashLower == "editprofileroute" -> EditProfileRoute

            hashLower == "settings/edithealth" -> EditHealthRoute
            hashLower == "settings/visits" -> VisitRoute
            hashLower == "settings/activedevices" -> ActiveDevicesRoute
            hashLower == "settings/blockedusers" -> BlockedUsersRoute
            hashLower == "settings/deleteaccount" -> DeleteAccountRoute
            hashLower == "settings/accountdeactivated" -> AccountDeactivatedRoute
            hashLower == "settings/privacypolicy" -> PrivacyPolicyRoute

            // 2. Eğer #profile/123 veya #profile/ahmet formatındaysa:
            hashLower.startsWith("profile/") -> {
                val parts = rawHash.substringAfter("/").split("/").filter { it.isNotEmpty() }
                val firstPart = parts.getOrNull(0)
                val parsedId = firstPart?.toLongOrNull()

                if (parsedId != null) {
                    val uName = parts.getOrNull(1) ?: ""
                    // Username varsa F5'te username üzerinden çekmesi için userId = -1L veriyoruz
                    if (uName.isNotBlank()) ProfileRoute(userId = -1L, username = uName)
                    else ProfileRoute(userId = parsedId, username = "")
                } else if (!firstPart.isNullOrBlank()) {
                    ProfileRoute(userId = -1L, username = firstPart)
                } else null
            }
            /*
            !rawHash.contains("/") -> {
                val cleanUsername = rawHash.removePrefix("@")
                if (myUsername != null && cleanUsername.equals(myUsername, ignoreCase = true)) {
                    ProfileGraphRoute
                } else {
                    ProfileRoute(userId = -1L, username = cleanUsername)
                }
            }

             */
            !rawHash.contains("/") -> {
                val cleanUsername = rawHash.removePrefix("@")
                if (myUsername != null && cleanUsername.equals(myUsername, ignoreCase = true)) {
                    ProfileGraphRoute
                } else {
                    ProfileRoute(userId = -1L, username = cleanUsername)
                }
            }

            else -> null
        }

        if (refreshRoute != null) {
            pendingRefreshRoute = refreshRoute
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
        pendingRefreshRoute = null
    }

}