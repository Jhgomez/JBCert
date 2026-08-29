package okik.tech.fullstack.feature.today.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.navigation.AppNavKey

@Serializable
sealed interface Today: AppNavKey

@Serializable
object TodayHome: Today

@Serializable
class TodayDetail(val apod: Apod?): Today {
    override val shouldNavIcon: Boolean = true
}