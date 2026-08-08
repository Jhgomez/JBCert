package okik.tech.fullstack

import okik.tech.fullstack.data.network.client.RemoteServerConfig
import kotlin.jvm.JvmInline

actual fun getPlatform(): Platform = Platform(RemoteServerConfig.WEB_URL)