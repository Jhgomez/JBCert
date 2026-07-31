package okik.tech.jetcert

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import okik.tech.jetcert.di.koinInit

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    koinInit()

    ComposeViewport {
    // You could use something like to actually show some loading state if needed
    // KoinMultiplatformApplication(config = createKoinConfiguration()) {
    //     var cacheReady by remember { mutableStateOf(false) }
    //
    //     LaunchedEffect(Unit) {
    //         val ksafe: KSafe = getKoin().get()
    //         ksafe.awaitCacheReady()
    //         cacheReady = true
    //     }
    //
    //     if (cacheReady) {
    //         AppContent() // your app's UI (without KoinMultiplatformApplication wrapper)
    //     }
    // }

        App()
    }
}