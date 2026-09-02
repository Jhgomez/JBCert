import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        KoinConfigurationKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}