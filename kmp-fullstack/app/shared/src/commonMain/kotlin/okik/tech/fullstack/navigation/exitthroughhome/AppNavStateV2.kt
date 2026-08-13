package okik.tech.fullstack.navigation.exitthroughhome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
fun rememberExitThroughHomeAppNavStateV2(
    homeKey: NavKey,
    topLevelKeys: Array<NavKey>
): ExitThroughHomeAppNavStateV2 {
    val nestedStack = topLevelKeys.associateWith { key ->
        rememberNavBackStack(
            configuration = SavedStateConfiguration {
                serializersModule = configs[topLevelKeys.indexOf(key)]
            },
            key
        )
    }

    val currentTopLevelRoute =
        rememberSerializable(configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(HomeList::class, HomeList.serializer())
                    subclass(TodayHome::class, TodayHome.serializer())
                    subclass(SearchHome::class, SearchHome.serializer())
                    subclass(AboutHome::class, AboutHome.serializer())
                }
            }
        }) {
            mutableStateOf(homeKey)
        }


    return remember {
        ExitThroughHomeAppNavStateV2(
            startRoute = homeKey,
            currentTopLevelRoute = currentTopLevelRoute,
            backStacks = nestedStack
        )
    }
}

class ExitThroughHomeAppNavStateV2(
    val startRoute: NavKey,
    currentTopLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute: NavKey by currentTopLevelRoute
    val stacksInUse: List<NavKey>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }

    @Composable
    fun decorateAndReturnNavEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): SnapshotStateList<NavEntry<NavKey>> {
        val decoratedEntries = backStacks.mapValues { (_, stack) ->
            val decorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            )
            rememberDecoratedNavEntries(
                backStack = stack, entryDecorators = decorators, entryProvider = entryProvider
            )
        }

        return stacksInUse.flatMap { decoratedEntries[it] ?: emptyList() }.toMutableStateList()
    }
}

class ExitThroughHomeNavigatorV2(private val state: ExitThroughHomeAppNavStateV2) {

    private val _reselectEvents = MutableSharedFlow<NavKey>(extraBufferCapacity = 1)
    val reselectEvents = _reselectEvents.asSharedFlow()



    fun navigate(route: NavKey){
        if (route in state.backStacks.keys){
            // This is a top level route, just switch to it
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun onReselect(route: NavKey) {
        _reselectEvents.tryEmit(route)
    }

    fun goBack(){
        val currentStack = state.backStacks[state.topLevelRoute] ?:
        error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute){
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}

