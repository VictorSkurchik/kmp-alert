import SwiftUI
import SharedUI

@main
struct iOSApp: App {
    init() {
        KoinKt.doInitKoin(config: nil)
    }

    var body: some Scene {
        WindowGroup {
            ComposeView()
                .ignoresSafeArea()
        }
    }
}
