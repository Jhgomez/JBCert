package okik.tech.fullstack.ui.adaptive.scenedecorators

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.window.core.layout.WindowSizeClass
import okik.tech.fullstack.ui.adaptive.cacheSize

class NavigationDecoratorScene(
    private val scene: Scene<NavKey>,
    private val windowSizeClass: WindowSizeClass,
    private val sharedTransitionScope: SharedTransitionScope,
    private val enableBackNavigation: MutableState<Boolean>,
    private val navBar: @Composable () -> Unit,
    private val navRail: @Composable () -> Unit,
    private val topBar:  @Composable () -> Unit,
    private val navIcon:  @Composable () -> Unit
) : Scene<NavKey> by scene {
    override val key = scene::class to scene.key

    override val content = @Composable {
        val animatedContentScope = LocalNavAnimatedContentScope.current
        val isMovableContentCaller =
            animatedContentScope.transition.targetState == EnterExitState.Visible

        with(sharedTransitionScope) {
            if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                Row(Modifier.fillMaxSize()) {
//                    Box(
//                        modifier = Modifier
////                            .cacheSize(!isMovableContentCaller)
//                            .sharedElement(
//                                rememberSharedContentState("nav-rail"),
//                                animatedContentScope
//                            )
//                    ) {
//                        if (isMovableContentCaller) {
//                            navRail()
//                        }
//                    }
                    Box(modifier = Modifier.weight(1f)) {
                        scene.content()
                    }
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    AnimatedVisibility(
                        visible = enableBackNavigation.value,
                        enter = fadeIn() + expandVertically(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        topBar()
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        scene.content()
                    }

                    Box(
                        modifier = Modifier
                            .cacheSize(!isMovableContentCaller)
                            .sharedElement(
                                rememberSharedContentState("nav-bar"),
                                animatedContentScope
                            )
                    ) {
                        if (isMovableContentCaller) {
                            navBar()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun rememberDecoratorStrategy(
    sharedTransitionScope: SharedTransitionScope,
    enableBackNavigation: MutableState<Boolean>,
    navBar: @Composable () -> Unit,
    navRail: @Composable () -> Unit,
    topBar:  @Composable () -> Unit,
    navIcon:  @Composable () -> Unit,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass,
): DecoratorStrategy {

    val currentNavBar by rememberUpdatedState(navBar)
    val currentNavRail by rememberUpdatedState(navRail)

    val movableNavBar = remember { movableContentOf { currentNavBar() } }
    val movableNavRail = remember { movableContentOf { currentNavRail() } }

    return remember(windowSizeClass, sharedTransitionScope) {
        DecoratorStrategy(
            windowSizeClass = windowSizeClass,
            sharedTransitionScope = sharedTransitionScope,
            navBar = movableNavBar,
            navRail = movableNavRail,
            enableBackNavigation = enableBackNavigation,
            topBar = topBar,
            navIcon = navIcon,
        )
    }
}

class DecoratorStrategy(
    private val windowSizeClass: WindowSizeClass,
    private val sharedTransitionScope: SharedTransitionScope,
    private val enableBackNavigation: MutableState<Boolean>,
    private val navBar: @Composable (() -> Unit),
    private val navRail: @Composable (() -> Unit),
    private val topBar:  @Composable () -> Unit,
    private val navIcon:  @Composable () -> Unit
): SceneDecoratorStrategy<NavKey> {
    override fun SceneDecoratorStrategyScope<NavKey>.decorateScene(
        scene: Scene<NavKey>
    ): Scene<NavKey> {
        return NavigationDecoratorScene(
            scene = scene,
            windowSizeClass = windowSizeClass,
            sharedTransitionScope = sharedTransitionScope,
            navBar = navBar,
            navRail = navRail,
            enableBackNavigation = enableBackNavigation,
            topBar = topBar,
            navIcon = navIcon,
        )
    }

}
