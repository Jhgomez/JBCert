package okik.tech.fullstack.feature.home.impl.apoddetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.copyright
import okik.tech.fullstack.Logger
import okik.tech.fullstack.domain.Apod
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ApodDetailScreen(
    apod: Apod,
    coilCacheKey: String?,
    keyExtras: Map<String, String>?,
    modifier: Modifier,
    goBack: () -> Unit
) {
    ApodDetailScreen(
        title = apod.title,
        date = apod.date,
        resourceUrl = apod.hdUrl,
        resourceType = apod.mediaType,
        description = apod.explanation,
        copyright = apod.copyright,
        coilCacheKey = coilCacheKey,
        keyExtras = keyExtras,
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
    modifier: Modifier,
    goBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(null) {
        Logger.logInfo("Coil cache", "Resource $resourceUrl cacheKey: $coilCacheKey")
        Logger.logInfo("Coil cache", "Resource $resourceUrl map: $keyExtras")
    }
    Column(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
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
                    Text(
                        text = title,
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

        Column (
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
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

            val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

            Spacer(Modifier.height(
                when {
                    sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND) ->
                        620.dp
                    sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ->
                        400.dp
                    else -> 200.dp
                }
            ))
        }
    }
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