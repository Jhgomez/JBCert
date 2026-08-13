package okik.tech.fullstack

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass

import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.about
import fullstack.app.shared.generated.resources.find
import fullstack.app.shared.generated.resources.gallery
import fullstack.app.shared.generated.resources.home
import fullstack.app.shared.generated.resources.info
import fullstack.app.shared.generated.resources.refresh
import fullstack.app.shared.generated.resources.search
import fullstack.app.shared.generated.resources.today
import okik.tech.fullstack.navigation.AboutHome
import okik.tech.fullstack.navigation.ExitThroughHomeNavigator
import okik.tech.fullstack.navigation.HomeApodDetail
import okik.tech.fullstack.navigation.HomeList
import okik.tech.fullstack.navigation.SearchDetail
import okik.tech.fullstack.navigation.SearchHome
import okik.tech.fullstack.navigation.TodayDetail
import okik.tech.fullstack.navigation.TodayHome
import okik.tech.fullstack.navigation.rememberExitThroughHomeAppNavState
import okik.tech.fullstack.ui.ApodViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

private enum class TopLevelRoute(
    val navKey: NavKey,
    val icon: DrawableResource,
    val description: StringResource
) {
    HOME(navKey = HomeList, icon = Res.drawable.home, description = Res.string.home),
    TODAY(navKey = TodayHome, icon = Res.drawable.today, description = Res.string.today),
    FIND(navKey = SearchHome, icon = Res.drawable.search, description = Res.string.find),
    ABOUT(navKey = AboutHome, icon = Res.drawable.info, description = Res.string.about)
}

class TopLevelBackStack<T: Any>(startKey: T) {

    // Maintain a stack for each top level route
    private var topLevelStacks : LinkedHashMap<T, SnapshotStateList<T>> = linkedMapOf(
        startKey to mutableStateListOf(startKey)
    )

    // Expose the current top level route for consumers
    var topLevelKey by mutableStateOf(startKey)
        private set

    // Expose the back stack so it can be rendered by the NavDisplay
    val backStack = mutableStateListOf(startKey)

    private fun updateBackStack() =
        backStack.apply {
            clear()
            addAll(topLevelStacks.flatMap { it.value })
        }

    fun addTopLevel(key: T){

        // If the top level doesn't exist, add it
        if (topLevelStacks[key] == null){
            topLevelStacks.put(key, mutableStateListOf(key))
        } else {
            // Otherwise just move it to the end of the stacks
            topLevelStacks.apply {
                remove(key)?.let {
                    put(key, it)
                }
            }
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: T){
        topLevelStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast(){
        val removedKey = topLevelStacks[topLevelKey]?.removeLastOrNull()
        // If the removed key was a top level key, remove the associated top level stack
        topLevelStacks.remove(removedKey)
        topLevelKey = topLevelStacks.keys.last()
        updateBackStack()
    }
}

@Composable
@Preview
fun App(
    viewModel: ApodViewModel = koinViewModel<ApodViewModel>(),
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
) {
//    windowSizeClass.isAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND,)


    MaterialTheme {
        val backStackState = rememberExitThroughHomeAppNavState(
            HomeList,
            TopLevelRoute.entries.map { route -> route.navKey }.toTypedArray()
        )

        val navigator = remember { ExitThroughHomeNavigator(backStackState) }

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    TopLevelRoute.entries.forEach { topLevelRoute ->

                        NavigationBarItem(
                            selected = topLevelRoute.navKey == backStackState.topLevelStack.last(),
                            onClick = {
                                navigator.navigate(topLevelRoute.navKey)
                            },
                            icon = {
                                Icon(
                                    imageVector = vectorResource(topLevelRoute.icon),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(stringResource(topLevelRoute.description))
                            },
                            colors = MaterialTheme.colorScheme.navigationBatItemColors
                        )
                    }
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(Res.string.gallery))
                    },
                    actions = {
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.refresh),
                                contentDescription = null
                            )
                        }
                    },
                    colors = MaterialTheme.colorScheme.topAppBarCustomColors
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->

            val entries = entryProvider {
                entry<HomeList> {
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

                entry<HomeApodDetail> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            navigator.navBack()
                        }) {
                            Text("Apod detail - bac")
                        }
                    }
                }

                entry<TodayHome> {
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

                entry<TodayDetail> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            navigator.navBack()
                        }) {
                            Text("Back to today home")
                        }
                    }
                }

                entry<SearchHome> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            navigator.navigate(SearchDetail(null))
                        }) {
                            Text("To Search Detail")
                        }
                    }
                }

                entry<SearchDetail> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            navigator.navBack()
                        }) {
                            Text("to search home")
                        }
                    }
                }

                entry<AboutHome> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("About")
                    }
                }
            }

            NavDisplay(
                modifier = Modifier.padding(innerPadding).consumeWindowInsets(WindowInsets.safeDrawing),
                entries = backStackState.decorateAndReturnNavEntries(entries),
                onBack = navigator::navBack,
            )
        }
    }
}

private var cachedNavItemColors: NavigationBarItemColors? = null

val ColorScheme.navigationBatItemColors: NavigationBarItemColors
    @Composable
    get() =  cachedNavItemColors ?:
        NavigationBarItemColors(
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,
            selectedIndicatorColor = onPrimaryContainer,
            unselectedIconColor = Color.LightGray,
            unselectedTextColor = Color.LightGray,
            disabledIconColor = Color.DarkGray,
            disabledTextColor = Color.DarkGray
        ).also {
            cachedNavItemColors = it
        }

private var cachedTopBarColors: TopAppBarColors? = null

val ColorScheme.topAppBarCustomColors: TopAppBarColors
    @Composable
    get() = cachedTopBarColors ?:
        TopAppBarColors(
            containerColor = primary,
            scrolledContainerColor = primaryContainer,
            navigationIconContentColor = onPrimary,
            titleContentColor = onPrimary,
            actionIconContentColor = onPrimary,
            subtitleContentColor = onPrimary,
        ).also {
            cachedTopBarColors = it
        }

@Composable
fun FullStackApp() {
//    movableContentOf {  }
//    NavMetadataKey
}