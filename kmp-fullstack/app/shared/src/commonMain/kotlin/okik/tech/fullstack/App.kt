package okik.tech.fullstack

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationItemColors
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailColors
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_LARGE_LOWER_BOUND

import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.about
import fullstack.app.shared.generated.resources.arrow_back
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
import okik.tech.fullstack.ui.adaptive.scenedecorators.rememberDecoratorStrategy
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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
@Preview
fun Top(viewModel: ApodViewModel = koinViewModel<ApodViewModel>()) {


    MaterialTheme {
        SharedTransitionLayout {
            val backStackState = rememberExitThroughHomeAppNavState(
                HomeList,
                TopLevelRoute.entries.map { route -> route.homeKey }.toTypedArray()
            )
            val navigator = remember { ExitThroughHomeNavigator(backStackState) }

            // my own list-detail strategy
            // val listDetailStrategy = rememberListDetailStrategy()

            // cmp built-in layout strategies, demonstrated here
            // https://github.com/terrakok/nav3-recipes/blob/master/sharedUI/src/commonMain/kotlin/com/example/nav3recipes/material/listdetail/MaterialListDetailActivity.kt
            val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
            val directive = remember(windowAdaptiveInfo) {
                calculatePaneScaffoldDirective(windowAdaptiveInfo)
                    .copy(horizontalPartitionSpacerSize = 0.dp)
            }
            val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(
                directive = directive,
                paneExpansionDragHandle = { state ->
                    val interactionSource = remember { MutableInteractionSource() }

                    VerticalDragHandle(
                        modifier =
                            Modifier.paneExpansionDraggable(
                                state,
                                LocalMinimumInteractiveComponentSize.current,
                                interactionSource
                            ),
                        interactionSource = interactionSource
                    )
                },
                paneExpansionState = rememberPaneExpansionState(
//                anchors = listOf(
//                    PaneExpansionAnchor.Proportion(0.25f),
//                    PaneExpansionAnchor.Proportion(0.5f),
//                    PaneExpansionAnchor.Proportion(0.75f),
//                )
                )
            )

            val decoratorStrategy = rememberDecoratorStrategy(
                sharedTransitionScope = this,
                enableBackNavigation = backStackState.shouldShowTopBar,
                navBar = {
                    BottomNavBar(
                        currentKey = backStackState.topLevelStack.lastOrNull(),
                        navigate = { navKey ->
                            navigator.navigate(navKey)
                        }
                    )
                },
                navRail = {

                },
                topBar = {
                    TopBar(goBack = navigator::goBack)
                },
                navIcon = {
                    IconButton(onClick = navigator::goBack) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                windowSizeClass = windowAdaptiveInfo.windowSizeClass
            )

            val entries = remember {
                entryProvider {
                    entry<HomeList>(
                        metadata = ListDetailSceneStrategy.listPane(
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
                        metadata = ListDetailSceneStrategy.detailPane()
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
            }

            NavDisplay(
                entries = backStackState.decorateAndReturnNavEntries(entries),
                sceneStrategies = listOf(listDetailStrategy),
                sceneDecoratorStrategies = listOf(decoratorStrategy),
                sharedTransitionScope = this,
                onBack = navigator::goBack
            )
        }
    }
}

@Composable
private fun TopBar(goBack: () -> Unit) {
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
        colors = MaterialTheme.colorScheme.topAppBarCustomColors,
        navigationIcon = {

            IconButton(onClick = goBack) {
                Icon(
                    imageVector = vectorResource(Res.drawable.arrow_back),
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun BottomNavBar(
    currentKey: NavKey?,
    navigate: (NavKey) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        TopLevelRoute.entries.forEach { topLevelRoute ->

            NavigationBarItem(
                selected = topLevelRoute.homeKey == currentKey,
                onClick = {
                    navigate(topLevelRoute.homeKey)
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