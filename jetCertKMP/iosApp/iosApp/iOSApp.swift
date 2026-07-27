import SwiftUI
import Shared

@main
struct iOSApp: SwiftUI.App {
    init() {
        KoinInitKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}