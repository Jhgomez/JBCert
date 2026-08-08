package okik.tech.fullstack

import okik.tech.fullstack.data.network.client.RemoteServerConfig
import platform.Foundation.NSProcessInfo

actual fun getPlatform(): Platform {
    val processInfo = NSProcessInfo.processInfo
    val isSimulator = processInfo.environment["SIMULATOR_DEVICE_NAME"] != null

    return Platform(
        if (isSimulator) {
            RemoteServerConfig.IOS_SIMULATOR_URL
        } else {
            RemoteServerConfig.DEVICE_URL
        }
    )
}