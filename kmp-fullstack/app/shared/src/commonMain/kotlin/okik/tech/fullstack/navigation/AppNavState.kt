package okik.tech.fullstack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import okik.tech.fullstack.domain.Apod

private val homeSerializerConfig = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(subclass = Home::class, serializer = Home.serializer())
    }
}

private val todaySerializerConfig = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(subclass = Today::class, serializer = Today.serializer())
    }
}

private val searchSerializerConfig = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(subclass = Search::class, serializer = Search.serializer())
    }
}

// careful, these should have the same order of the topLevel array param in RememberExitThroughHomeAppNavState
private val configs = arrayOf(homeSerializerConfig, todaySerializerConfig, searchSerializerConfig)

@Composable
fun rememberExitThroughHomeAppNavState(
    homeKey: NavKey,
    topLevelKeys: Array<NavKey>
): ExitThroughHomeAppNavState {
    val nestedStack = Array<Stack>(size = topLevelKeys.size) { index ->
        val navStack = rememberNavBackStack(
            configuration = SavedStateConfiguration {
                serializersModule = configs[index]
            },
            topLevelKeys[index]
        )

        Stack(
            key = topLevelKeys[index],
            nestedStack = navStack
        )
    }

    // I could switch to `rememberNavBackStack`
    val topLevelStack = rememberSerializable(serializer = SnapshotStateListSerializer()) {
        mutableStateListOf<NavKey>(HomeList)
    }

    return remember {
        ExitThroughHomeAppNavState(
            homeKey = homeKey,
            topLevelStack = topLevelStack,
            nestedStack = nestedStack,
            topLevelKeys = topLevelKeys
        )
    }
}

class ExitThroughHomeAppNavState(
    val homeKey: NavKey,
    val topLevelStack: SnapshotStateList<NavKey>,
    val nestedStack: Array<Stack>,
    val topLevelKeys: Array<NavKey>
) {

    @Composable
    fun decorateAndReturnNavEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): SnapshotStateList<NavEntry<NavKey>> {
        val navEntries = nestedStack.associate { stack ->
            val decorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
                rememberViewModelStoreNavEntryDecorator<NavKey>(),
            )

            stack.key to rememberDecoratedNavEntries(
                backStack = stack.nestedStack,
                entryDecorators = decorators,
                entryProvider = entryProvider
            )
        }

        return topLevelStack.flatMap { key -> navEntries[key] ?: emptyList() }.toMutableStateList()
    }
}

class Stack(
    val key: NavKey,
    val nestedStack: NavBackStack<NavKey>
)

@Serializable
sealed interface Home: NavKey

@Serializable
object HomeList: Home

@Serializable
class HomeApodDetail(val apod: Apod): Home

@Serializable
sealed interface Today: NavKey

@Serializable
object TodayHome: Today

@Serializable
class TodayDetail(val apod: Apod): Today

@Serializable
sealed interface Search: NavKey

@Serializable
object SearchHome: Search

@Serializable
class SearchDetail(val apod: Apod): Search

class ExitThroughHomeNavigator(private val state: ExitThroughHomeAppNavState) {

    fun naviGate(key: NavKey) {
//        val stack = rememberNavBackStack(
//            configuration = SavedStateConfiguration {
//                serializersModule = homeSerializerConfig + todaySerializerConfig + searchSerializerConfig
//            }
//        )
        state.topLevelStack.clear()

        if (key in state.topLevelKeys) {
            if (key != state.homeKey) {
                state.topLevelStack.add(key)
            } else {
                state.topLevelStack.add(state.homeKey)
                state.topLevelStack.add(key)
            }
        } else {
            val nestedStack = state
                .nestedStack
                .firstOrNull { stack ->  key::class.isInstance(stack.key)  }!!
                .nestedStack

//            nestedStack.remove(key)
            nestedStack.add(key)
        }


    }

    fun navBack() {
        val currentTopLevelKey = state.topLevelStack.last()

        val currentNestedStack = state
            .nestedStack
            .firstOrNull { stack -> currentTopLevelKey::class.isInstance(stack) }!!
            .nestedStack

        if (currentTopLevelKey != currentNestedStack.last()) {
            currentNestedStack.removeLast()
        } else {
            state.topLevelStack.removeLast()
        }
    }
}

class AppNavState(
    val currentStack: NavBackStack<NavKey>,
    val nestedStacks: Map<NavKey, NavBackStack<NavKey>>,
    var currentTopRoute: MutableState<NavKey>
)

