package okik.tech.jetcert

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import okik.tech.jetcert.di.di.koinInit

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    koinInit()

    ComposeViewport {
        App()
    }
}