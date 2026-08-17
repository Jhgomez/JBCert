package okik.tech.fullstack.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asAndroidBitmap
import coil3.Image
import coil3.asImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.imageResource

@Composable
private actual fun getPlatformCoilDefaultImages(
    defaultError: DrawableResource,
    defaultPlaceholder: DrawableResource,
    defaultFallback: DrawableResource
): CoilDefaultImages {
    val error = imageResource(defaultError)
    val placeholder = imageResource(defaultPlaceholder)
    val fallback = imageResource(defaultFallback)

    return remember {
        CoilDefaultImages(
            defaultError = error.asAndroidBitmap().asImage(),
            defaultPlaceholder = placeholder.asAndroidBitmap().asImage(),
            defaultFallback = fallback.asAndroidBitmap().asImage()
        )
    }
}