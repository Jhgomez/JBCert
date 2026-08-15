package okik.tech.fullstack.feature.today.impl

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
import okik.tech.fullstack.feature.search.api.SearchDetail
import okik.tech.fullstack.feature.search.api.SearchHome
import okik.tech.fullstack.feature.today.api.TodayDetail
import okik.tech.fullstack.feature.today.api.TodayHome
import okik.tech.fullstack.navigation.exitthroughhome.ExitThroughHomeNavigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.todayEntries(navigator: ExitThroughHomeNavigator) {
    entry<TodayHome>(
        metadata = ListDetailSceneStrategy.listPane(
            sceneKey = TodayHome,
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
                navigator.navigate(TodayDetail(null))
            }) {
                Text("To TOday detail")
            }
        }
    }

    entry<TodayDetail>(
        metadata = ListDetailSceneStrategy.detailPane(sceneKey = TodayHome)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                navigator.goBack()
            }) {
                Text("Back to today home")
            }
        }
    }
}