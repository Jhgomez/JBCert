package okik.tech.fullstack.navigation

import androidx.navigation3.runtime.NavKey
import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.about
import fullstack.app.shared.generated.resources.find
import fullstack.app.shared.generated.resources.home
import fullstack.app.shared.generated.resources.info
import fullstack.app.shared.generated.resources.search
import fullstack.app.shared.generated.resources.today
import okik.tech.fullstack.feature.about.api.AboutHome
import okik.tech.fullstack.feature.home.api.HomeList
import okik.tech.fullstack.feature.search.api.SearchHome
import okik.tech.fullstack.feature.today.api.TodayHome
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class TopLevelRoute(
    val homeKey: NavKey,
    val icon: DrawableResource,
    val description: StringResource
) {
    HOME(
        homeKey = HomeList,
        icon = Res.drawable.home,
        description = Res.string.home
    ),
    TODAY(
        homeKey = TodayHome,
        icon = Res.drawable.today,
        description = Res.string.today
    ),
    FIND(
        homeKey = SearchHome,
        icon = Res.drawable.search,
        description = Res.string.find
    ),
    ABOUT(
        homeKey = AboutHome,
        icon = Res.drawable.info,
        description = Res.string.about
    )
}