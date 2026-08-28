package okik.tech.fullstack.feature.home.impl.apoddetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.arrow_back
import fullstack.app.shared.generated.resources.gallery
import fullstack.app.shared.generated.resources.refresh
import okik.tech.fullstack.Logger
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.topAppBarCustomColors
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ApodDetailScreen(
    apod: Apod,
    coilCacheKey: String?,
    keyExtras: Map<String, String>?,
    modifier: Modifier,
    goBack: () -> Unit
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()

    ApodDetailScreen(
        title = apod.title,
        date = apod.date,
        resourceUrl = apod.hdUrl,
        resourceType = apod.mediaType,
        description = apod.explanation,
        copyright = apod.copyright,
        coilCacheKey = coilCacheKey,
        keyExtras = keyExtras,
        shouldShowBackIcon = !windowAdaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        ),
        modifier = modifier,
        goBack = goBack
    )
}

@Composable
fun ApodDetailScreen(
    title: String,
    date: String,
    resourceUrl: String?,
    resourceType: String,
    description: String,
    copyright: String?,
    coilCacheKey: String?,
    keyExtras: Map<String, String>?,
    shouldShowBackIcon: Boolean,
    modifier: Modifier,
    goBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(null) {
        Logger.logInfo("Coil cache", "Resource $resourceUrl cacheKey: $coilCacheKey")
        Logger.logInfo("Coil cache", "Resource $resourceUrl map: $keyExtras")
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(resourceUrl)
                        .placeholderMemoryCacheKey(coilCacheKey)
                        .memoryCacheKeyExtras(keyExtras ?: emptyMap())
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )
                TopAppBar(
                    title = {
                        Text(title)
                    },
                    colors = MaterialTheme.colorScheme.topAppBarCustomColorsTwo,
                    navigationIcon = {
                        if (shouldShowBackIcon) {
                            IconButton(onClick = goBack) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.arrow_back),
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    expandedHeight = 240.dp
                )
            }
        },
        content = { innerPadding ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(innerPadding)
            ) {
                items(
                    count = 40,
                    key = { it }
                ) { index ->
                    Text(
                        text = "Es el item $index",
                        style = MaterialTheme.typography.titleLargeEmphasized
                    )
                }
            }
        }
    )
}

private var cachedTopBarColors: TopAppBarColors? = null

val ColorScheme.topAppBarCustomColorsTwo: TopAppBarColors
    @Composable
    get() = cachedTopBarColors ?:
    TopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
        navigationIconContentColor = onPrimary,
        titleContentColor = onPrimary,
        actionIconContentColor = onPrimary,
        subtitleContentColor = onPrimary,
    ).also {
        cachedTopBarColors = it
    }