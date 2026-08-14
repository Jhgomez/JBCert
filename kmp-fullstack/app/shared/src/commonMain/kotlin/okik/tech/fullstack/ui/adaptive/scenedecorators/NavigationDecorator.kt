package okik.tech.fullstack.ui.adaptive.scenedecorators

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_LARGE_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

class NavigationDecoratorScene(
    scene: Scene<NavKey>,
    private val windowSizeClass: WindowSizeClass,
    private val sharedTransitionScope: SharedTransitionScope,
    private val enableBackNavigation: Boolean,
    private val navBar: @Composable () -> Unit,
    private val navRail: @Composable () -> Unit,
    private val topBar:  @Composable () -> Unit,
    private val navIcon:  @Composable () -> Unit
) : Scene<NavKey> by scene {

    override val content = @Composable {
        val animatedContentScope = LocalNavAnimatedContentScope.current
        val isMovableContentCaller =
            animatedContentScope.transition.targetState == EnterExitState.Visible

        with(sharedTransitionScope) {
            when {
                windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                    scene.content()
                }

                windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_LARGE_LOWER_BOUND) -> {
                    scene.content()
                }

                else -> {
                    Column (Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState("top-bar"),
                                    animatedContentScope
                                )
                        ) {
                            if (
//                                isMovableContentCaller &&
                                enableBackNavigation) {
                                topBar()
                            }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            scene.content()
                        }
                        Box(
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState("nav-rail"),
                                    animatedContentScope
                                )
                        ) {
//                            if (isMovableContentCaller) {
//                                navBar()
//                            }
                            topBar()
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
    enableBackNavigation: Boolean,
    navBar: @Composable () -> Unit,
    navRail: @Composable () -> Unit,
    topBar:  @Composable () -> Unit,
    navIcon:  @Composable () -> Unit
): DecoratorStrategy {
   val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    return remember(sizeClass) {
        DecoratorStrategy(
            windowSizeClass = sizeClass,
            sharedTransitionScope = sharedTransitionScope,
            navBar = navBar,
            navRail = navRail,
            enableBackNavigation = enableBackNavigation,
            topBar = topBar,
            navIcon = navIcon,
        )
    }
}

class DecoratorStrategy(
    private val windowSizeClass: WindowSizeClass,
    private val sharedTransitionScope: SharedTransitionScope,
    private val enableBackNavigation: Boolean,
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
