package okik.tech.fullstack.navigation.exitthroughhome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun rememberExitThroughHomeAppNavStateV3(
    homeKey: NavKey,
    topLevelKeys: Array<NavKey>
): ExitThroughHomeAppNavStateV3 {
    val nestedStack = topLevelKeys.associateWith { key ->
        rememberNavBackStack(
            configuration = SavedStateConfiguration {
                serializersModule = configs[topLevelKeys.indexOf(key)]
            },
            key
        )
    }

    val topLevelStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(HomeList::class, HomeList.serializer())
                    subclass(TodayHome::class, TodayHome.serializer())
                    subclass(SearchHome::class, SearchHome.serializer())
                    subclass(AboutHome::class, AboutHome.serializer())
                }
            }
        },
        homeKey
    )


    return remember(homeKey, topLevelKeys) {
        ExitThroughHomeAppNavStateV3(
            startKey = homeKey,
            topLevelStack = topLevelStack,
            subStacks = nestedStack
        )
    }
}

/**
 * This one also exists through home but it doesn't remove other top level entries backstack
 * unless home is tapped again
 */
class ExitThroughHomeAppNavStateV3(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    val currentTopLevelKey: NavKey by derivedStateOf { topLevelStack.last() }

    val topLevelKeys
        get() = subStacks.keys

    val currentSubStack: NavBackStack<NavKey>
        get() = subStacks[currentTopLevelKey]
            ?: error("Sub stack for $currentTopLevelKey does not exist")

    val currentKey: NavKey by derivedStateOf { currentSubStack.last() }

    @Composable
    fun decorateAndReturnNavEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): SnapshotStateList<NavEntry<NavKey>> {
        val decoratedEntries = subStacks.mapValues { (_, stack) ->
            val decorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            )
            rememberDecoratedNavEntries(
                backStack = stack, entryDecorators = decorators, entryProvider = entryProvider
            )
        }

        return topLevelStack.flatMap { decoratedEntries[it] ?: emptyList() }.toMutableStateList()
    }
}

class ExitThroughHomeNavigatorV3(private val state: ExitThroughHomeAppNavStateV3) {

    /**
     * Navigate to a navigation key
     *
     * @param key - the navigation key to navigate to.
     */
    fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
    }

    /**
     * Go back to the previous navigation key.
     */
    fun goBack() {
        when (state.currentKey) {
            state.startKey -> error("You cannot go back from the start route")
            state.currentTopLevelKey -> {
                // We're at the base of the current sub stack, go back to the previous top level
                // stack.
                state.topLevelStack.removeLastOrNull()
            }
            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    /**
     * Go to a non top level key.
     */
    private fun goToKey(key: NavKey) {
        state.currentSubStack.apply {
            // Remove it if it's already in the stack so it's added at the end.
            remove(key)
            add(key)
        }
    }

    /**
     * Go to a top level stack.
     */
    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            if (key == state.startKey) {
                // This is the start key. Clear the stack so it's added as the only key.
                clear()
            } else {
                // Remove it if it's already in the stack so it's added at the end.
                remove(key)
            }
            add(key)
        }
    }

    /**
     * Clearing all but the root key in the current sub stack.
     */
    private fun clearSubStack() {
        state.currentSubStack.run {
            if (size > 1) subList(1, size).clear()
        }
    }
}

