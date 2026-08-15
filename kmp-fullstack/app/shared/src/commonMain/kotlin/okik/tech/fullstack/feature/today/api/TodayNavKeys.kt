package okik.tech.fullstack.feature.today.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import okik.tech.fullstack.domain.Apod

@Serializable
sealed interface Today: NavKey

@Serializable
object TodayHome: Today

@Serializable
class TodayDetail(val apod: Apod?): Today