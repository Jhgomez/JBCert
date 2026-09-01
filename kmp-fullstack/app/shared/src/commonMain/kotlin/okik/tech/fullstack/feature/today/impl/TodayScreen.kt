package okik.tech.fullstack.feature.today.impl

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.copyright
import okik.tech.fullstack.Logger
import okik.tech.fullstack.feature.home.impl.apoddetail.topAppBarCustomColorsTwo
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TodayScreen(
    modifier: Modifier = Modifier,
    viewModel: TodayViewModel = koinViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(null) {
        // not launching this method on the vm init because it seems to cause an subtle error just
        // as stated here https://developer.android.com/topic/architecture/ui-layer/state-production#initializing-state-production
        viewModel.getTodayApod()
    }

    TodayScreen(
        title = state.value.apod?.title,
        date = state.value.apod?.date,
        resourceUrl = state.value.apod?.hdUrl,
        resourceType = state.value.apod?.mediaType,
        description = state.value.apod?.explanation,
        copyright = state.value.apod?.copyright,
        modifier = modifier
    )
}

@Composable
fun TodayScreen(
    title: String?,
    date: String?,
    resourceUrl: String?,
    resourceType: String?,
    description: String?,
    copyright: String?,
    modifier: Modifier
) {
    var cacheKey: MutableState<MemoryCache.Key?> = remember { mutableStateOf(null) }
    val keyExtras: Map<String, String>? = remember { null }
    val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    SharedTransitionLayout {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        LaunchedEffect(cacheKey, keyExtras) {
            Logger.logInfo("Coil cache", "Resource $resourceUrl cacheKey: $cacheKey")
            Logger.logInfo("Coil cache", "Resource $resourceUrl map: $keyExtras")
        }

        AnimatedContent(
            targetState = sizeClass
        ) { size ->
            if (!size.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                SmallSizeScreen(
                    modifier,
                    scrollBehavior,
                    resourceUrl,
                    { coilCacheKey -> if (cacheKey.value == null) cacheKey.value = coilCacheKey },
                    { cacheKey.value },
                    this,
                    title,
                    date,
                    description,
                    copyright,
                    sizeClass
                )
            } else {
                MediumAndLargeSizeScreen(
                    modifier,
                    resourceUrl,
                    { coilCacheKey -> if (cacheKey.value == null) cacheKey.value = coilCacheKey },
                    { cacheKey.value },
                    this,
                    title,
                    date,
                    description,
                    copyright,
                    sizeClass
                )
            }
        }
    }

}

@Composable
fun SharedTransitionScope.MediumAndLargeSizeScreen(
    modifier: Modifier,
    resourceUrl: String?,
    onSaveCoilCacheKey: (MemoryCache.Key?) -> Unit,
    coilCacheKey: () -> MemoryCache.Key?,
    animatedContentScope: AnimatedContentScope,
    title: String?,
    date: String?,
    description: String?,
    copyright: String?,
    sizeClass: WindowSizeClass
) {
    Row(modifier = modifier) {
        Column(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(.65f)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(resourceUrl)
                    .memoryCacheKey(coilCacheKey())
                    .build(),
                onSuccess = { onSaveCoilCacheKey(it.result.memoryCacheKey) },
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedElement(
                        sharedContentState = rememberSharedContentState("apod-image"),
                        animatedVisibilityScope = animatedContentScope
                    )
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(0.35f)
                .fillMaxHeight()
                .padding(horizontal = 24.dp)
        ) {
            if (title != null && date != null && description != null) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLargeEmphasized
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmallEmphasized,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = description,
                    modifier = Modifier.fillMaxSize(),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )

                Spacer(Modifier.height(24.dp))
            }

            if (copyright != null) {
                Row {
                    Icon(
                        imageVector = vectorResource(Res.drawable.copyright),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = copyright,
                        style = MaterialTheme.typography.labelMediumEmphasized
                    )
                }
            }
        }
    }
}

@Composable
private fun SharedTransitionScope.SmallSizeScreen(
    modifier: Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    resourceUrl: String?,
    onSaveCoilCacheKey: (MemoryCache.Key?) -> Unit,
    coilCacheKey: () -> MemoryCache.Key?,
    animatedContentScope: AnimatedContentScope,
    title: String?,
    date: String?,
    description: String?,
    copyright: String?,
    sizeClass: WindowSizeClass
) {
    Column(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(resourceUrl)
                    .memoryCacheKey(coilCacheKey())
                    .build(),
                onSuccess = { onSaveCoilCacheKey(it.result.memoryCacheKey) },
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .matchParentSize()
                    .sharedElement(
                        sharedContentState = rememberSharedContentState("apod-image"),
                        animatedVisibilityScope = animatedContentScope
                    )
            )
            TopAppBar(
                title = {
                    Text(
                        text = title ?: "",
                        modifier = Modifier.fillMaxWidth().padding(start = 32.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLargeEmphasized
                    )
                },
                colors = MaterialTheme.colorScheme.topAppBarCustomColorsTwo,
                scrollBehavior = scrollBehavior,
                expandedHeight = 280.dp
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = date ?: "",
                style = MaterialTheme.typography.labelSmallEmphasized,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = description ?: "",
                modifier = Modifier.fillMaxSize(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )

            Spacer(Modifier.height(24.dp))

            if (copyright != null) {
                Row {
                    Icon(
                        imageVector = vectorResource(Res.drawable.copyright),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = copyright,
                        style = MaterialTheme.typography.labelMediumEmphasized
                    )
                }
            }

            if (!sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                Spacer(Modifier.height(200.dp))
            }
        }
    }
}