package okik.tech.fullstack.navigation

import androidx.navigation3.runtime.NavKey

interface AppNavKey: NavKey {
    /**
     * In our case, in any screen that is not displaying nav rail(screens with a width lower than
     * WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) this flags causes a different visual top
     * navigation UI, shouldShowTopBar shows a top bar while, shouldNavIcon shows a nav icon, they
     * are not exclusive so if you set both to true in a small screen width you will see both
     * components, but in screen configurations greater than WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
     * they won't change any behaviour, that means they both end up displaying the same navigation UI
     * which is Navigation Icon, so in those configuration it doesn't matter if both or just one of
     * them is true, you will end up with same top navigation UI(Navigation Icon). So they are useful
     * to accomplish screens that have a collapsible toolbar, this removes responsibility of handling
     * back navigation from each destination that has a custom top bar without checking the current
     * widht configuration
     */
    val shouldShowTopBar: Boolean
        get() = false
    val shouldNavIcon: Boolean
        get() = false
}