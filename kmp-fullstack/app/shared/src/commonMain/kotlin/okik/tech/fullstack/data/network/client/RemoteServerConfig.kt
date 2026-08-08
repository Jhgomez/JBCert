package okik.tech.fullstack.data.network.client

object RemoteServerConfig {
    //    private const val SERVER_MODE = "broken"
    private const val SERVER_MODE = "working"

    private const val WORKING_SERVER = "localhost"
    private const val BROKEN_SERVER = "fake-server"

    private const val PORT = "8080"

    private val NON_PHYSICAL_DEVICE =
        if (SERVER_MODE == "working") "$WORKING_SERVER:$PORT" else "$BROKEN_SERVER:$PORT"
    val EMULATOR_URL = if (SERVER_MODE == "working") "10.0.2.2:$PORT" else "$BROKEN_SERVER:$PORT"
    val IOS_SIMULATOR_URL = NON_PHYSICAL_DEVICE
    val DEVICE_URL = NON_PHYSICAL_DEVICE
    val DESKTOP_URL = NON_PHYSICAL_DEVICE
    val WEB_URL = NON_PHYSICAL_DEVICE
}