package okik.tech.fullstack

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import okik.tech.fullstack.di.initKoin

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Fullstack",
    ) {
        App()
    }
}