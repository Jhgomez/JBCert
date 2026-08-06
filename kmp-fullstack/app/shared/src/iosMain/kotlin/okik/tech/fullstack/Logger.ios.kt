package okik.tech.fullstack

import platform.Foundation.NSLog
import platform.Foundation.NSLogv

actual object Logger {
    actual fun logInfo(tag: String, message: String) {
        NSLog("Info: $tag - $message")
    }

    actual fun logError(tag: String, message: String) {
        NSLog("Error: $tag - $message")
    }
}