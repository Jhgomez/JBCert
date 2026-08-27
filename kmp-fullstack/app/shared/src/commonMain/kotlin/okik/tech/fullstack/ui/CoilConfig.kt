package okik.tech.fullstack.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import coil3.ColorImage
import coil3.Image
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.memory.MemoryCache
import coil3.network.cachecontrol.CacheControlCacheStrategy
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.error
import fullstack.app.shared.generated.resources.fallback
import fullstack.app.shared.generated.resources.placeholder
import io.ktor.client.HttpClient
import okik.tech.fullstack.data.network.client.engineFactory
import org.jetbrains.compose.resources.DrawableResource

data class CoilDefaultImages(
    val defaultError: Image,
    val defaultPlaceholder: Image,
    val defaultFallback: Image
)

val ColorScheme.defaultError: Image
    get() = ColorImage(onErrorContainer.toArgb())

val ColorScheme.defaultPlaceholder: Image
    get() = ColorImage(primaryContainer.toArgb())

val ColorScheme.defaultFallback: Image
    get() = ColorImage(secondaryFixedDim.toArgb())

@Composable
expect fun getPlatformCoilDefaultImages(
    defaultError: DrawableResource,
    defaultPlaceholder: DrawableResource,
    defaultFallback: DrawableResource
): CoilDefaultImages

/**
 * Helps configure a default ImageLoader that all coil images, that don't specify an ImageLoader,
 * will use
 */
@Composable
fun IntiCoilImageLoader() {
    val defaults = getPlatformCoilDefaultImages(
        defaultError = Res.drawable.error,
        defaultPlaceholder = Res.drawable.placeholder,
        defaultFallback = Res.drawable.fallback
    )

    // you could also use SingletonImageLoader.setSafe with PlatformContext.INSTANCE or
    // LocalPlatformContext.current
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.02)
                    .build()
            }
            .components {
                add(KtorNetworkFetcherFactory(
                    httpClient = {
                        return@KtorNetworkFetcherFactory HttpClient(engineFactory) {
//                            defaultRequest {
//                                // tells intermediaries not to serve a cached response but if targeting
//                                // web it is important, if this is set then a preflight is performed
//                                // and asks permission for cache-control but if the server headers
//                                // don't have this configured then the request is blocked in the browser
//                                // in our case it would be allowHeader(HttpHeaders.CacheControl)
//                                header("Cache-Control", "no-cache")
//                            }
                        }
                    },
                    cacheStrategy = { CacheControlCacheStrategy() }
                ))
            }
            .logger(DebugLogger())
            .error(defaults.defaultError)
            .fallback(defaults.defaultFallback)
            .placeholder(defaults.defaultPlaceholder)
            .crossfade(true)
            .build()
    }
}