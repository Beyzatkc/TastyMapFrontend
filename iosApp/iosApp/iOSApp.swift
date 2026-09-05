import SwiftUI

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {

        // Bildirim delegate'lerini bağlıyoruz
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self

        application.registerForRemoteNotifications()
        return true
    }

    // 2. Kullanıcı bildirime tıkladığı an burası çalışır
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
}
@main
struct iOSApp: App {
init() {
        FirebaseApp.configure()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
            .onOpenURL { url in
               DeepLinkManager.shared.handleLink(url: url.absoluteString)
            }
        }
    }
}