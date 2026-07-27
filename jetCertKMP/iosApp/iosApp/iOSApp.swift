import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        IosKoinInitKt.initializeKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}