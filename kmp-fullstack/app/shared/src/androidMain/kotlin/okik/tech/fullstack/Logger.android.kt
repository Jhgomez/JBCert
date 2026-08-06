package okik.tech.fullstack

import android.util.Log

actual object Logger {
    actual fun logInfo(tag: String, message: String) {
        Log.i(tag, message)
    }

    actual fun logError(tag: String, message: String) {
        Log.d(tag, message)
    }
}