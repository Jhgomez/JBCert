package okik.tech.fullstack.navigation.combinedflattenedbackstack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import okik.tech.fullstack.navigation.exitthroughhome.AboutHome
import okik.tech.fullstack.navigation.exitthroughhome.HomeList
import okik.tech.fullstack.navigation.exitthroughhome.SearchHome
import okik.tech.fullstack.navigation.exitthroughhome.TodayHome
import okik.tech.fullstack.navigation.exitthroughhome.configs


@Composable
fun rememberExitThroughHomeAppNavStateV4(
    startKey: NavKey,
    topLevelKeys: Array<NavKey>
): ExitThroughHomeAppNavStateV4 {

    val nestedStacks = topLevelKeys.associateWith { key ->
        rememberNavBackStack(
            configuration = SavedStateConfiguration {
                serializersModule = configs[topLevelKeys.indexOf(key)]
            },
            key
        )
    }


    val topLevelEntries = rememberNavBackStack(
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
        startKey
    )

    return remember {
        ExitThroughHomeAppNavStateV4(
            nestedStacks = nestedStacks,
            topLevelEntries = topLevelEntries,
            topLevelKeys = topLevelKeys
        )
    }
}

/**
 * This state maintains a separate back stack for each top-level destination (tab), It keeps track
 * of the order of selected top level routes and it flattens the navEntry list based on the order
 * of the top leve routes so If I go A-B-C-D-A the I have all nested stacks of each top level
 * entry, so if I want to exit the app until all entries have been popped out, then I would go in
 * this order A-D-C-B, so I exit on B, similarly A-B-C-D-A-C-B  then I exit like B-C-A-D
 */
class ExitThroughHomeAppNavStateV4(
    val nestedStacks: Map<NavKey, NavBackStack<NavKey>>,
    val topLevelEntries: NavBackStack<NavKey>,
    val topLevelKeys: Array<NavKey>
) {

    @Composable
    fun decorateAndReturnNavEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): SnapshotStateList<NavEntry<NavKey>> {
        val decoratedEntries = nestedStacks.mapValues { (_, stack) ->
            val decorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            )
            rememberDecoratedNavEntries(
                backStack = stack, entryDecorators = decorators, entryProvider = entryProvider
            )
        }

        return topLevelEntries.flatMap { decoratedEntries[it] ?: emptyList() }.toMutableStateList()
    }
}

class ExitThroughHomeNavigatorV4(private val state: ExitThroughHomeAppNavStateV4) {

    private val _reselectEvents = MutableSharedFlow<NavKey>(extraBufferCapacity = 1)
    val reselectEvents = _reselectEvents.asSharedFlow()

    fun navigate(key: NavKey) {

        if (key in state.topLevelKeys) {
            state.topLevelEntries.remove(key)
            state.topLevelEntries.add(key)
        } else {
            state.nestedStacks[state.topLevelEntries.last()]?.add(key)
        }
    }

    fun onReselect(route: NavKey) {
        _reselectEvents.tryEmit(route)
    }

    fun goBack(){
        val key = state.nestedStacks[state.topLevelEntries.last()]!!.removeLastOrNull()
        state.topLevelEntries.remove(key)
    }
}

