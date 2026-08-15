package okik.tech.fullstack.feature.home.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import okik.tech.fullstack.feature.about.api.AboutHome
import okik.tech.fullstack.feature.home.api.HomeApodDetail
import okik.tech.fullstack.feature.home.api.HomeList
import okik.tech.fullstack.navigation.exitthroughhome.ExitThroughHomeNavigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.homeEntries(navigator: ExitThroughHomeNavigator) {
    entry<HomeList>(
        metadata = ListDetailSceneStrategy.listPane(
            sceneKey = HomeList,
            detailPlaceholder = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("My PlaceHolder")
                }
            }
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                navigator.navigate(HomeApodDetail(null))
            }) {
                Text("To Apod")
            }
        }
    }

    entry<HomeApodDetail>(
        metadata = ListDetailSceneStrategy.detailPane(
            sceneKey = HomeList
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                navigator.goBack()
            }) {
                Text("Apod detail - bac")
            }
        }
    }
}