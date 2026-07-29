package okik.tech.jetcert

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import okik.tech.jetcert.di.di.initKoinWeb

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    initKoinWeb()

    ComposeViewport {
        App()
    }
}