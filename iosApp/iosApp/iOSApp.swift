import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    init() {
        FirebaseApp.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    OrgBeemTastymapCoreNavigationDeepLinkManager.shared.handleLink(url: url.absoluteString)
                }
        }
    }
}