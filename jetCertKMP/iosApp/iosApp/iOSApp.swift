import SwiftUI
import Shared

@main
struct iOSApp: SwiftUI.App {
    init() {
        IosKoinInitKt.doInitializeKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}