package okik.tech.fullstack

import okik.tech.fullstack.data.network.client.RemoteServerConfig


actual fun getPlatform(): Platform = Platform(RemoteServerConfig.DESKTOP_URL)