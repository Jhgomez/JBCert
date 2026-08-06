package okik.tech.fullstack

// you could use kermit instead, this time we will avoid 3rd party just for loggin
expect object Logger {
    fun logInfo(tag: String, message: String)
    fun logError(tag: String, message: String)
}