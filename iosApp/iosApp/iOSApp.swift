import SwiftUI
import SharedLogic

@main
struct iOSApp: App {
    private let appContainer = AppContainer()

    var body: some Scene {
        WindowGroup {
            ContentView(appContainer: appContainer)
                .task {
                    _ = try? await appContainer.notificationPermissionManager.requestPermission()
                }
        }
    }
}
