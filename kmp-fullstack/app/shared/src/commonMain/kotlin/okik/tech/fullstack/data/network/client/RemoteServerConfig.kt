package okik.tech.fullstack.data.network.client

object RemoteServerConfig {
    //    private const val SERVER_MODE = "broken"
    private const val SERVER_MODE = "working"

    // the IP assign to you by your DHCP server(AKA, your ISP router) or change it to "localhost"
    private const val WORKING_SERVER = "192.168.1.15"
    private const val BROKEN_SERVER = "fake-server"

    private const val PORT = "7070"

    private val NON_PHYSICAL_DEVICE =
        if (SERVER_MODE == "working") "$WORKING_SERVER:$PORT" else "$BROKEN_SERVER:$PORT"
    val EMULATOR_URL = if (SERVER_MODE == "working") "10.0.2.2:$PORT" else "$BROKEN_SERVER:$PORT"
    val IOS_SIMULATOR_URL = NON_PHYSICAL_DEVICE
    val DEVICE_URL = NON_PHYSICAL_DEVICE
    val DESKTOP_URL = NON_PHYSICAL_DEVICE
    val WEB_URL = NON_PHYSICAL_DEVICE
}