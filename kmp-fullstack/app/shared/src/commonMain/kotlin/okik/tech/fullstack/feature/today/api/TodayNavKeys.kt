package okik.tech.fullstack.feature.today.api

import kotlinx.serialization.Serializable
import okik.tech.fullstack.navigation.AppNavKey

@Serializable
sealed interface Today: AppNavKey

@Serializable
object TodayHome: Today
