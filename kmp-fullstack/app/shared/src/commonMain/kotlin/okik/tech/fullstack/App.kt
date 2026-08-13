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
import okik.tech.fullstack.navigation.exitthroughhome.AboutHome
import okik.tech.fullstack.navigation.exitthroughhome.ExitThroughHomeNavigator
import okik.tech.fullstack.navigation.exitthroughhome.HomeApodDetail
import okik.tech.fullstack.navigation.exitthroughhome.HomeList
import okik.tech.fullstack.navigation.exitthroughhome.SearchDetail
import okik.tech.fullstack.navigation.exitthroughhome.SearchHome
import okik.tech.fullstack.navigation.exitthroughhome.TodayDetail
import okik.tech.fullstack.navigation.exitthroughhome.TodayHome
import okik.tech.fullstack.navigation.exitthroughhome.rememberExitThroughHomeAppNavState
import okik.tech.fullstack.ui.ApodViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

private enum class TopLevelRoute(
    val homeKey: NavKey,
    val icon: DrawableResource,
    val description: StringResource
) {
    HOME(
        homeKey = HomeList,
        icon = Res.drawable.home,
        description = Res.string.home
    ),
    TODAY(
        homeKey = TodayHome,
        icon = Res.drawable.today,
        description = Res.string.today
    ),
    FIND(
        homeKey = SearchHome,
        icon = Res.drawable.search,
        description = Res.string.find
    ),
    ABOUT(
        homeKey = AboutHome,
        icon = Res.drawable.info,
        description = Res.string.about
    )
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
            TopLevelRoute.entries.map { route -> route.homeKey }.toTypedArray()
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
                            selected = topLevelRoute.homeKey == backStackState.topLevelStack.last(),
                            onClick = {
                                navigator.navigate(topLevelRoute.homeKey)
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
                            navigator.goBack()
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
                            navigator.goBack()
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
                            navigator.goBack()
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
                onBack = navigator::goBack,
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