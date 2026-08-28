package okik.tech.fullstack.feature.home.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import okik.tech.fullstack.feature.home.api.HomeApodDetail
import okik.tech.fullstack.feature.home.api.HomeList
import okik.tech.fullstack.feature.home.impl.apoddetail.ApodDetailPlaceholder
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


//        Column(
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            var placeholder: MemoryCache.Key? = remember { null }
////            var bitmap: MutableState<Painter> = remember { mutableStateOf(ColorPainter(Color.LightGray)) }
//
////            LaunchedEffect(navigator) {
////                bitmap.value = BitmapPainter(Res.readBytes("drawable/placeholder.webp").decodeToImageBitmap())
////            }
//
//
//
//            Button(onClick = {
//                navigator.navigate(HomeApodDetail(null))
//            }) {
//                Text("To Apod")
//            }
//        }
    }

    entry<HomeApodDetail>(
        metadata = ListDetailSceneStrategy.detailPane(
            sceneKey = HomeList
        )
    ) { key ->
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                navigator.goBack()
            }) {
                Text(key.apod.toString())
            }
        }
    }
}