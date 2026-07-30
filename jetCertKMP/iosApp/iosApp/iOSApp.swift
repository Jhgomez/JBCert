import SwiftUI
import Shared

@main
struct iOSApp: SwiftUI.App {
    init() {
        InitKoinIosKt.doInit()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}