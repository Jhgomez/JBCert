package okik.tech.fullstack.feature.search.impl

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import coil3.memory.MemoryCache
import kotlinx.datetime.LocalDate
import okik.tech.fullstack.Logger

@Composable
fun SearchScreen() {


}

@Composable
fun SearchScreen(
    modifier: Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    resourceUrl: String?,
    onSaveCoilCacheKey: (MemoryCache.Key?) -> Unit,
    coilCacheKey: () -> MemoryCache.Key?,
    onDatePicked: (LocalDate) -> Unit,
    animatedContentScope: AnimatedContentScope,
    title: String?,
    date: String?,
    description: String?,
    copyright: String?,
    sizeClass: WindowSizeClass,
    scrollState: ScrollState
) {
    val cacheKey: MutableState<MemoryCache.Key?> = remember { mutableStateOf(null) }
    val keyExtras: Map<String, String>? = remember { null }
    val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    SharedTransitionLayout {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val scrollState: ScrollState = rememberScrollState()

        LaunchedEffect(cacheKey, keyExtras) {
            Logger.logInfo("Coil cache", "Resource $resourceUrl cacheKey: $cacheKey")
            Logger.logInfo("Coil cache", "Resource $resourceUrl map: $keyExtras")
        }


    }
}