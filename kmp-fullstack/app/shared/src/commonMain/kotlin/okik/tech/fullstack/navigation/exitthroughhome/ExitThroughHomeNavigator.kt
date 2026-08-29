package okik.tech.fullstack.navigation.exitthroughhome

import androidx.navigation3.runtime.NavKey
import io.ktor.util.reflect.instanceOf
import okik.tech.fullstack.navigation.AppNavKey

class ExitThroughHomeNavigator(private val state: ExitThroughHomeAppNavState) {

    fun navigate(key: NavKey) {
        if (key !is AppNavKey) throw IllegalStateException("All keys should extend from AppNavKey")

        if (key in state.topLevelKeys) {
            state.topLevelStack.clear()

            if (key == state.homeKey) {
                state.topLevelStack.add(key)
            } else {
                state.topLevelStack.add(state.homeKey)
                state.topLevelStack.add(key)
            }
        } else {
            var currentNestedStack = state
                .nestedStacks
                .firstOrNull { stack ->  stack.key == state.topLevelStack.lastOrNull() }!!
                .nestedStack

            if (currentNestedStack.last().instanceOf(key::class))
                currentNestedStack.removeLastOrNull()

            currentNestedStack.add(key)
        }

        state.shouldShowTopBar.value = key.shouldShowTopBar
        state.shouldShowNavIcon.value = key.shouldNavIcon
    }

    fun goBack() {
        val currentTopLevelKey = state.topLevelStack.last()

        val currentNestedStack = state
            .nestedStacks
            .firstOrNull { stack -> stack.key == currentTopLevelKey }!!
            .nestedStack

        if (currentNestedStack.last() in state.topLevelKeys) {
            state.topLevelStack.removeLastOrNull()
        } else {
            currentNestedStack.removeLastOrNull()
        }

        val currentKey = state
            .nestedStacks
            .firstOrNull { stack -> stack.key == state.topLevelStack.last() }!!
            .nestedStack
            .last() as AppNavKey

        state.shouldShowTopBar.value = currentKey.shouldShowTopBar
        state.shouldShowNavIcon.value = currentKey.shouldNavIcon
    }
}

