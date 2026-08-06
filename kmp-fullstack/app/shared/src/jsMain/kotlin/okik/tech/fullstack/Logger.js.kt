package okik.tech.fullstack

actual object Logger {
    actual fun logInfo(tag: String, message: String) {
        console.info("$tag - $message")
    }

    actual fun logError(tag: String, message: String) {
        console.error("$tag $message")
    }
}