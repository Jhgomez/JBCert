package okik.tech.fullstack.feature.search.impl

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import okik.tech.fullstack.feature.search.api.SearchHome
import okik.tech.fullstack.navigation.exitthroughhome.ExitThroughHomeNavigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.searchEntries(navigator: ExitThroughHomeNavigator) {
    entry<SearchHome> {
        SearchScreen()
    }
}