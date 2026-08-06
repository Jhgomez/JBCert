package okik.tech.fullstack

expect object Logger {
    fun logInfo(tag: String, message: String)
    fun logError(tag: String, message: String)
}