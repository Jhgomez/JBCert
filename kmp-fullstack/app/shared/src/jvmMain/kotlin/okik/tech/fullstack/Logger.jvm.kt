package okik.tech.fullstack

import java.util.logging.Logger

actual object Logger {
    private val jvmLogger = Logger.getLogger(okik.tech.fullstack.Logger::class.java.name)

    actual fun logInfo(tag: String, message: String) {
        jvmLogger.info("%s - %s".format(tag, message))
    }

    actual fun logError(tag: String, message: String) {
        jvmLogger.severe("%s - %s".format(tag, message))g
    }
}