package okik.tech.jetcert

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import okik.tech.jetcert.di.initKoinJvm

fun main() = application {
    initKoinJvm()

    Window(
        onCloseRequest = ::exitApplication,
        title = "JetCert",
    ) {
        App()
    }
}