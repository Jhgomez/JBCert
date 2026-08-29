package okik.tech.fullstack.feature.about.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import okik.tech.fullstack.navigation.AppNavKey

@Serializable
sealed interface About: AppNavKey


@Serializable
object AboutHome: About