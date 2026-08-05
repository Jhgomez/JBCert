package okik.tech.fullstack

import platform.Foundation.NSProcessInfo

value class IOSPlatform(private val baseUrl: String) : Platform {

    override fun getBaseUrl(): String = baseUrl
}

actual fun getPlatform(): Platform {
    val processInfo = NSProcessInfo.processInfo
    val isSimulator = processInfo.environment["SIMULATOR_DEVICE_NAME"] != null

    return IOSPlatform(
        if (isSimulator) {
            RemoteServerConfig.IOS_SIMULATOR_URL
        } else {
            RemoteServerConfig.DEVICE_URL
        }
    )
}