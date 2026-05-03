import SwiftUI

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