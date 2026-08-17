package okik.tech.fullstack.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.DrawableResource

// In all other targets we use imageResource() which use a ImageBitmap under the hood, it means we are
// reading bytes(I/O) and according to KMP's resource documentation, this happens synchronously in all
// platform but web, in which happens asynchronously, it returns some empty Painter and when it is
// ready then it returns the actual bitmap which is used in subsequent recompositions but the problem
// with Coil is that it doesn't recompose when the resource is finally available, so we rather use
// Coil's built in ColorImage for web only, Image in Coil only has two implementation at this moment
// BitmapImage or ColorImage, we could use compose's API "preloadImageBitmap" which we could use
// to try to recompose as it returns a state to us, but I don't want to go that route no
// https://kotlinlang.org/docs/multiplatform/compose-multiplatform-resources-usage.html#images
@Composable
actual fun getPlatformCoilDefaultImages(
    defaultError: DrawableResource,
    defaultPlaceholder: DrawableResource,
    defaultFallback: DrawableResource
): CoilDefaultImages {
    val scheme = MaterialTheme.colorScheme

    return remember {
        CoilDefaultImages(
            defaultError = scheme.defaultError,
            defaultPlaceholder = scheme.defaultPlaceholder,
            defaultFallback = scheme.defaultFallback
        )
    }
}
