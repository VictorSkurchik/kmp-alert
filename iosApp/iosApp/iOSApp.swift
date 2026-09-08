import SwiftUI
import SharedUI

@main
struct iOSApp: App {
    private let appContainer = AppContainer()

    var body: some Scene {
        WindowGroup {
            ComposeView(viewModel: appContainer.createMonitoringViewModel())
                .ignoresSafeArea()
                .task {
                    _ = try? await appContainer.notificationPermissionManager.requestPermission()
                }
        }
    }
}
