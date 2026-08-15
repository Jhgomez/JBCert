package okik.tech.fullstack.navigation.exitthroughhome

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.ktor.util.reflect.instanceOf

class ExitThroughHomeNavigator(private val state: ExitThroughHomeAppNavState) {

    fun navigate(key: NavKey) {
        var currentNestedStack: NavBackStack<NavKey>? = null

        if (key in state.topLevelKeys) {
            state.topLevelStack.clear()

            if (key == state.homeKey) {
                state.topLevelStack.add(key)
            } else {
                state.topLevelStack.add(state.homeKey)
                state.topLevelStack.add(key)
            }
        } else {
            currentNestedStack = state
                .nestedStack
                .firstOrNull { stack ->  stack.key == state.topLevelStack.lastOrNull() }!!
                .nestedStack

            if (currentNestedStack.last().instanceOf(key::class))
                currentNestedStack.removeLastOrNull()

            currentNestedStack.add(key)
        }


        currentNestedStack = currentNestedStack ?: state
            .nestedStack
            .firstOrNull { stack ->  stack.key == state.topLevelStack.lastOrNull() }!!
            .nestedStack

        state.shouldShowTopBar.value = currentNestedStack.last() !in state.topLevelKeys
    }

    fun goBack() {
        val currentTopLevelKey = state.topLevelStack.last()

        val currentNestedStack = state
            .nestedStack
            .firstOrNull { stack -> stack.key == currentTopLevelKey }!!
            .nestedStack

        if (currentNestedStack.last() in state.topLevelKeys) {
            state.topLevelStack.removeLastOrNull()
        } else {
            currentNestedStack.removeLastOrNull()
        }

        state.shouldShowTopBar.value = state
            .nestedStack
            .firstOrNull { stack -> stack.key == state.topLevelStack.last() }!!
            .nestedStack
            .last() !in state.topLevelKeys
    }
}

