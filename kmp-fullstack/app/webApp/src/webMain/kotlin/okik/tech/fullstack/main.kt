package okik.tech.fullstack

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import okik.tech.fullstack.di.WebMainInitKoin
import okik.tech.fullstack.di.initKoin

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    WebMainInitKoin()

    ComposeViewport {
        App()
    }
}