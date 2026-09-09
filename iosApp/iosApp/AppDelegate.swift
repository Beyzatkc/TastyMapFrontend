import SwiftUI
import UserNotifications
import FirebaseMessaging

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {

        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self

        // Bildirim İzni İsteme
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, _ in
            if granted {
                DispatchQueue.main.async {
                    application.registerForRemoteNotifications()
                }
            }
        }

        return true
    }

    // 1. UYGULAMA ÖN PLANDAYKEN BİLDİRİM GELDİĞİNDE ÇALIŞIR
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {

        IOSNotificationHelper.shared.updateBadge(hasUnread: true)

        if #available(iOS 14.0, *) {
            completionHandler([.banner, .sound, .badge])
        } else {
            completionHandler([.alert, .sound, .badge])
        }
    }

    // 🔥 2. ARKA PLANDA / KAPALIYKEN DATA PAYLOAD (SILENT/BACKGROUND PUSH) GELDİĞİNDE ÇALIŞIR
    func application(_ application: UIApplication,
                     didReceiveRemoteNotification userInfo: [AnyHashable : Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {

        // Rozeti güncelle
        IOSNotificationHelper.shared.updateBadge(hasUnread: true)

        // Uygulama arka plandaysa yerel bildirim (Local Notification) oluşturup gösteriyoruz
        if application.applicationState != .active {
            let title = userInfo["title"] as? String ?? "TastyMap"
            let body = userInfo["body"] as? String ?? "Yeni bildirim var"

            let content = UNMutableNotificationContent()
            content.title = title
            content.body = body
            content.sound = .default
            content.userInfo = userInfo

            let request = UNNotificationRequest(
                identifier: UUID().uuidString,
                content: content,
                trigger: nil // Anında göster
            )

            UNUserNotificationCenter.current().add(request)
        }

        completionHandler(.newData)
    }

    // 3. Kullanıcı bildirime tıkladığı an çalışır
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {

        let userInfo = response.notification.request.content.userInfo
        print("FCM_IOS_DEBUG: Bildirime tıklandı, payload: \(userInfo)")

        if let type = userInfo["type"] as? String {
            if type == "FOLLOW_REQUEST" || type == "NEW_FOLLOWER" {
                if let userId = userInfo["userId"] as? String {
                    let profileLink = "https://coleman-nonethic-marinda.ngrok-free.dev/profile/\(userId)"
                    OrgBeemTastymapCoreNavigationDeepLinkManager.shared.handleLink(url: profileLink)
                }
            } else if type == "SECURITY_ALERT" {
                let securityLink = "https://coleman-nonethic-marinda.ngrok-free.dev/settings/security"
                OrgBeemTastymapCoreNavigationDeepLinkManager.shared.handleLink(url: securityLink)
            }
        }

        completionHandler()
    }

    // 4. FCM TOKEN YENİLENDİĞİNDE / İLK OLUŞTUĞUNDA ÇALIŞIR
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let token = fcmToken else { return }
        print("iOS FCM Token Alındı/Yenilendi: \(token)")

        IOSNotificationHelper.shared.updateFcmToken(token: token)
    }
}