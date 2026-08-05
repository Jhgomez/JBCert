package okik.tech.fullstack

import android.os.Build
import kotlin.String

@JvmInline
value class AndroidPlatform(private val baseUrl: String) : Platform {
    override fun getBaseUrl(): String = baseUrl
}

actual fun getPlatform(): Platform {
    val isEmulator = Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86") ||
            Build.MANUFACTURER.contains("Genymotion") ||
            Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
            "google_sdk" == Build.PRODUCT ||
            Build.HARDWARE.contains("goldfish") ||
            Build.HARDWARE.contains("ranchu")

    return AndroidPlatform(
        if (isEmulator) {
            RemoteServerConfig.EMULATOR_URL
        } else {
            RemoteServerConfig.DEVICE_URL
        }
    )
}