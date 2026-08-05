package okik.tech.fullstack

object RemoteServerConfig {
    //    private const val SERVER_MODE = "broken"
    private const val SERVER_MODE = "working"

    private const val WORKING_SERVER = "localhost"
    private const val BROKEN_SERVER = "fake-server"

    private const val PORT = "8080"

    private val NON_PHYSICAL_DEVICE =
        if (SERVER_MODE == "working") "http://$WORKING_SERVER:$PORT" else "http://$BROKEN_SERVER:$PORT"
    val EMULATOR_URL = NON_PHYSICAL_DEVICE
    val IOS_SIMULATOR_URL = NON_PHYSICAL_DEVICE
    val DEVICE_URL = NON_PHYSICAL_DEVICE
    val DESKTOP_URL = NON_PHYSICAL_DEVICE
    val WEB_URL = NON_PHYSICAL_DEVICE
}