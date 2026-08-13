package okik.tech.fullstack.ui.adaptive.sceneresolution

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import okik.tech.fullstack.ui.adaptive.sceneresolution.ListDetailStrategy.ListKey

fun listPane(placeHolder: @Composable () -> Unit) = metadata {
    put(ListDetailStrategy.ListKey, placeHolder)
}

fun detailPane() = metadata {
    put(ListDetailStrategy.DetailKey, true)
}

private class ListDetailScene(
    override val key: Any,
    override val previousEntries: List<NavEntry<NavKey>>,
    val listEntry: NavEntry<NavKey>,
    val detailEntry: NavEntry<NavKey>?,
) : Scene<NavKey> {
    override val entries: List<NavEntry<NavKey>> = listOf(listEntry, detailEntry).filterNotNull()

    override val content  = @Composable {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(.4f)) {
                listEntry.Content()
            }
            Box(modifier = Modifier.weight(.6f)) {
                detailEntry?.Content() ?: listEntry.metadata[ListKey]!!.invoke()
            }
        }
    }
}

@Composable
fun rememberListDetailStrategy(): ListDetailStrategy {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    return remember(windowSizeClass) {
        ListDetailStrategy(windowSizeClass)
    }
}

class ListDetailStrategy(private val windowSizeClass: WindowSizeClass) : SceneStrategy<NavKey> {
    override fun SceneStrategyScope<NavKey>.calculateScene(
        entries: List<NavEntry<NavKey>>
    ): Scene<NavKey>? {

        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        val detailEntry =
            entries.lastOrNull()?.takeIf { entry -> entry.metadata.contains(DetailKey) }

        val indexToLookListKeyAt = if (detailEntry != null) {
            entries.size - 2
        } else {
            (entries.size - 1).coerceAtLeast(0)
        }

        val listEntry = entries
            .getOrNull(indexToLookListKeyAt)
            ?.takeIf { entry -> entry.metadata.contains(ListKey) }
            ?: return null

        return ListDetailScene(
            key = listEntry.contentKey,
            previousEntries = entries.dropLast(1),
            listEntry = listEntry,
            detailEntry = detailEntry
        )
    }

    object ListKey : NavMetadataKey<@Composable () -> Unit>
    object DetailKey : NavMetadataKey<Boolean>
}