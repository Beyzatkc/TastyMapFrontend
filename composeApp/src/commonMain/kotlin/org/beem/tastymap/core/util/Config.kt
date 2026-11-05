package org.beem.tastymap.core.util

object AppConfig {

 enum class Environment(
  val host: String,
  val isSecure: Boolean
 ) {
  // 1. Web (Chrome/Wasm/JS) üzerinden yerel geliştirme:
  DEV_WEB(host = "localhost:8080", isSecure = false),

  // 2. Android Emülatör üzerinden yerel geliştirme:
  DEV_ANDROID(host = "10.0.2.2:8080", isSecure = false),

  // 3. Canlı / Ngrok / SSL tüneli:
  PROD_NGROK(host = "coleman-nonethic-marinda.ngrok-free.dev", isSecure = true),

  PROD_NGROK_E(host = "shaneka-unfactual-shaneka.ngrok-free.dev", isSecure = true)
 }

 // === AKTİF ORTAM SEÇİMİ ===
 // Çalıştığın ortama göre sadece burayı değiştirmen yeterlidir:
 var currentEnv: Environment = Environment.PROD_NGROK

 // Protokoller güvenliğe (isSecure) göre otomatik belirlenir
 val httpProtocol: String get() = if (currentEnv.isSecure) "https" else "http"
 val wsProtocol: String get() = if (currentEnv.isSecure) "wss" else "wss"
 val apiHost: String get() = currentEnv.host

 // Ktor Client ve genel istekler için ortak taban adresler
 val BASE_URL: String get() = "$httpProtocol://$apiHost/"

 fun getApiBaseUrl(): String = "$httpProtocol://$apiHost"

 fun getWebSocketUrl(deviceId: String): String =
  "$wsProtocol://$apiHost/ws/auth/$deviceId"

 // İhtiyaç duyulursa BuildKonfig'den gelen değerlere de doğrudan erişim:
 val vapidKey: String get() = BuildKonfig.VAPID_KEY
}