package okik.tech.fullstack.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import org.jetbrains.compose.resources.DrawableResource

/**
 * Helps configure a default ImageLoader that all coil images, that don't specify an ImageLoader,
 * will use
 */
@Composable
fun IntiCoilImageLoader() {

    // you could also use SingletonImageLoader.setSafe with PlatformContext.INSTANCE or
    // LocalPlatformContext.current
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.02)
                    .build()
            }
//            .diskCache(null)
//            .diskCache {
//                DiskCache.Builder()
//                    .directory()
//                    .maxSizePercent(0.002)
//                    .build()
//            }
            .components {
                add(KtorNetworkFetcherFactory(
                    httpClient = {
                        return@KtorNetworkFetcherFactory HttpClient {
                            defaultRequest {
                                header("Cache-Control", "no-cache")
                            }
                        }
                    },
                    cacheStrategy = { CacheControlCacheStrategy() }
                ))
            }
            .logger(DebugLogger())
//            .error(defaultError)
//            .fallback(defaultFallback)
//            .placeholder(defaultPlaceholder)
            .crossfade(true)
            .build()
    }
}