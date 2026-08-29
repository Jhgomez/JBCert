package okik.tech.fullstack.feature.home.impl

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import okik.tech.fullstack.feature.home.api.HomeApodDetail
import okik.tech.fullstack.feature.home.api.HomeList
import okik.tech.fullstack.feature.home.impl.apoddetail.ApodDetailPlaceholder
import okik.tech.fullstack.feature.home.impl.apoddetail.ApodDetailScreen
import okik.tech.fullstack.feature.home.impl.apodlist.HomeListScreen
import okik.tech.fullstack.navigation.exitthroughhome.ExitThroughHomeNavigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.homeEntries(
    navigator: ExitThroughHomeNavigator
) {
    entry<HomeList>(
        metadata = ListDetailSceneStrategy.listPane(
            sceneKey = HomeList,
            detailPlaceholder = {
                ApodDetailPlaceholder(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        )
    ) {
        HomeListScreen(
            onApodClick = { apod, key ->
                navigator.navigate(
                    key = HomeApodDetail(
                        apod = apod,
                        coilCacheKey = key?.key,
                        keyExtras = key?.extras
                    )
                )
            }
        )
    }

    entry<HomeApodDetail>(
        metadata = ListDetailSceneStrategy.detailPane(
            sceneKey = HomeList
        )
    ) { key ->
        ApodDetailScreen(
            apod = key.apod,
            coilCacheKey = key.coilCacheKey,
            keyExtras = key.keyExtras,
            modifier = Modifier.fillMaxSize()
        )
    }
}