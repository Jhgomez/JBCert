import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        SharedModuleKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}