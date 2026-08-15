package okik.tech.fullstack.navigation.exitthroughhome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import androidx.savedstate.serialization.SavedStateConfiguration
import io.ktor.util.reflect.instanceOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import okik.tech.fullstack.domain.Apod


class Stack(
    val key: NavKey,
    val nestedStack: NavBackStack<NavKey>
)

@Serializable
sealed interface Home: NavKey

@Serializable
object HomeList: Home

@Serializable
class HomeApodDetail(val apod: Apod?): Home {
}

@Serializable
sealed interface Today: NavKey

@Serializable
object TodayHome: Today

@Serializable
class TodayDetail(val apod: Apod?): Today

@Serializable
sealed interface Search: NavKey

@Serializable
object SearchHome: Search

@Serializable
class SearchDetail(val apod: Apod?): Search


private val homeSerializerConfig = SerializersModule {
    polymorphic(NavKey::class) {
        subclassesOfSealed<Home>()
    }
}

private val todaySerializerConfig = SerializersModule {
    polymorphic(NavKey::class) {
        subclassesOfSealed<Today>()
    }
}

private val searchSerializerConfig = SerializersModule {
    polymorphic(NavKey::class) {
        subclassesOfSealed<Search>()
    }
}

private val aboutSerializerConfig = SerializersModule {
    polymorphic(NavKey::class) {
        subclassesOfSealed<About>()
    }
}

// careful, these should have the same order of the topLevel array param in RememberExitThroughHomeAppNavState
val configs = arrayOf(homeSerializerConfig, todaySerializerConfig, searchSerializerConfig, aboutSerializerConfig)

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

    val shouldShowTopBar = rememberSerializable {
        mutableStateOf(false)
    }

    // I could switch to `rememberNavBackStack`
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

    return remember {
        ExitThroughHomeAppNavState(
            homeKey = homeKey,
            topLevelStack = topLevelStack,
            nestedStack = nestedStack,
            topLevelKeys = topLevelKeys,
            shouldShowTopBar = shouldShowTopBar
        )
    }
}

/**
 * Exists through home, if select home and there is other top level entry stack, the other
 * stack is removed and only home is left, so if I navigate from A->B->A, then only A stack exists
 * after navigating, if I navigate from one top level entry other that home the newest top level
 * entry replaces the previous one, so navigating from A->B->C only leaves us with A and X
 * stacks, the nested stack are not cleared but only removed from the available stacks when
 * navigating back, if you have interacted with a stack that is not currently available when
 * you select it back its nested stack state is preserved
 */
class ExitThroughHomeAppNavState(
    val homeKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val nestedStack: Array<Stack>,
    val topLevelKeys: Array<NavKey>,
    val shouldShowTopBar: MutableState<Boolean>
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
