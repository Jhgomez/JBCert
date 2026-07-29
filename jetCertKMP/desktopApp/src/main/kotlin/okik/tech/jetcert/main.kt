package okik.tech.jetcert

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import okik.tech.jetcert.di.initKoinJvm

suspend fun main() {
    initKoinJvm()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "JetCert",
        ) {
            App()
        }
    }
}