package okik.tech.fullstack

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailColors
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_LARGE_LOWER_BOUND

import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.arrow_back
import fullstack.app.shared.generated.resources.gallery
import fullstack.app.shared.generated.resources.refresh
import okik.tech.fullstack.feature.about.impl.aboutEntry
import okik.tech.fullstack.feature.home.api.HomeList
import okik.tech.fullstack.feature.home.impl.homeEntries
import okik.tech.fullstack.feature.search.impl.searchEntries
import okik.tech.fullstack.feature.today.impl.todayEntries
import okik.tech.fullstack.navigation.TopLevelRoute
import okik.tech.fullstack.navigation.exitthroughhome.ExitThroughHomeNavigator
import okik.tech.fullstack.navigation.exitthroughhome.rememberExitThroughHomeAppNavState
import okik.tech.fullstack.ui.ApodViewModel
import okik.tech.fullstack.ui.adaptive.scenedecorators.rememberDecoratorStrategy
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
@Preview
fun App(viewModel: ApodViewModel = koinViewModel<ApodViewModel>()) {
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

            val navRailState = rememberWideNavigationRailState()

            LaunchedEffect(windowAdaptiveInfo) {
                navRailState.snapTo(
                    if (windowAdaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_LARGE_LOWER_BOUND)) {
                        WideNavigationRailValue.Expanded
                    } else {
                        WideNavigationRailValue.Collapsed
                    }
                )
            }

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
                    NavRail(
                        currentKey = backStackState.topLevelStack.lastOrNull(),
                        navigate = { navKey ->
                            navigator.navigate(navKey)
                        },
                        navRailState = navRailState
                    )
                },
                topBar = {
                    TopBar(goBack = navigator::goBack)
                },
                navIcon = {
                    IconButton(onClick = navigator::goBack) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                windowSizeClass = windowAdaptiveInfo.windowSizeClass
            )

            val entries = remember {
                entryProvider {
                    homeEntries(navigator)

                    todayEntries(navigator)

                    searchEntries(navigator)

                    aboutEntry()
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

@Composable
fun NavRail(
    currentKey: NavKey?,
    navigate: (NavKey) -> Unit,
    navRailState: WideNavigationRailState
) {

    WideNavigationRail(
        state = navRailState,
        colors = MaterialTheme.colorScheme.navigationRailColors
    ) {
        TopLevelRoute.entries.forEach { topLevelRoute ->

            WideNavigationRailItem(
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
                colors = MaterialTheme.colorScheme.navigationRailItemColors,
                railExpanded = navRailState.currentValue == WideNavigationRailValue.Expanded
            )
        }
    }
}

private var cachedNavRailColor: WideNavigationRailColors? = null

val ColorScheme.navigationRailColors: WideNavigationRailColors
    @Composable
    get() = cachedNavRailColor ?:
    WideNavigationRailColors(
        containerColor = primary,
        contentColor = Color.White,
        modalContainerColor = primary,
        modalScrimColor = Color.LightGray,
        modalContentColor = Color.White
    )

private var cachedNavRailItemColors: NavigationItemColors? = null

val ColorScheme.navigationRailItemColors: NavigationItemColors
    @Composable
    get() =  cachedNavRailItemColors ?:
    NavigationItemColors(
        selectedIconColor = Color.White,
        selectedTextColor = Color.White,
        selectedIndicatorColor = onPrimaryContainer,
        unselectedIconColor = Color.LightGray,
        unselectedTextColor = Color.LightGray,
        disabledIconColor = Color.DarkGray,
        disabledTextColor = Color.DarkGray
    ).also {
        cachedNavRailItemColors = it
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

val Background = Color(red = 254, green = 247, blue = 255)
val Error = Color(red = 179, green = 38, blue = 30)
val ErrorContainer = Color(red = 249, green = 222, blue = 220)
val InverseOnSurface = Color(red = 245, green = 239, blue = 247)
val InversePrimary = Color(red = 208, green = 188, blue = 255)
val InverseSurface = Color(red = 50, green = 47, blue = 53)
val OnBackground = Color(red = 29, green = 27, blue = 32)
val OnError = Color(red = 255, green = 255, blue = 255)
val OnErrorContainer = Color(red = 65, green = 14, blue = 11)
val OnPrimary = Color(red = 255, green = 255, blue = 255)
val OnPrimaryContainer = Color(red = 33, green = 0, blue = 93)
val OnPrimaryFixed = Color(red = 33, green = 0, blue = 93)
val OnPrimaryFixedVariant = Color(red = 79, green = 55, blue = 139)
val OnSecondary = Color(red = 255, green = 255, blue = 255)
val OnSecondaryContainer = Color(red = 29, green = 25, blue = 43)
val OnSecondaryFixed = Color(red = 29, green = 25, blue = 43)
val OnSecondaryFixedVariant = Color(red = 74, green = 68, blue = 88)
val OnSurface = Color(red = 29, green = 27, blue = 32)
val OnSurfaceVariant = Color(red = 73, green = 69, blue = 79)
val OnTertiary = Color(red = 255, green = 255, blue = 255)
val OnTertiaryContainer = Color(red = 49, green = 17, blue = 29)
val OnTertiaryFixed = Color(red = 49, green = 17, blue = 29)
val OnTertiaryFixedVariant = Color(red = 99, green = 59, blue = 72)
val Outline = Color(red = 121, green = 116, blue = 126)
val OutlineVariant = Color(red = 202, green = 196, blue = 208)
val Primary = Color(red = 103, green = 80, blue = 164)
val PrimaryContainer = Color(red = 234, green = 221, blue = 255)
val PrimaryFixed = Color(red = 234, green = 221, blue = 255)
val PrimaryFixedDim = Color(red = 208, green = 188, blue = 255)
val Scrim = Color(red = 0, green = 0, blue = 0)
val Secondary = Color(red = 98, green = 91, blue = 113)
val SecondaryContainer = Color(red = 232, green = 222, blue = 248)
val SecondaryFixed = Color(red = 232, green = 222, blue = 248)
val SecondaryFixedDim = Color(red = 204, green = 194, blue = 220)
val Surface = Color(red = 254, green = 247, blue = 255)
val SurfaceBright = Color(red = 254, green = 247, blue = 255)
val SurfaceContainer = Color(red = 243, green = 237, blue = 247)
val SurfaceContainerHigh = Color(red = 236, green = 230, blue = 240)
val SurfaceContainerHighest = Color(red = 230, green = 224, blue = 233)
val SurfaceContainerLow = Color(red = 247, green = 242, blue = 250)
val SurfaceContainerLowest = Color(red = 255, green = 255, blue = 255)
val SurfaceDim = Color(red = 222, green = 216, blue = 225)
val SurfaceTint = Primary
val SurfaceVariant = Color(red = 231, green = 224, blue = 236)
val Tertiary = Color(red = 125, green = 82, blue = 96)
val TertiaryContainer = Color(red = 255, green = 216, blue = 228)
val TertiaryFixed = Color(red = 255, green = 216, blue = 228)
val TertiaryFixedDim = Color(red = 239, green = 184, blue = 200)