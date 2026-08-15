package okik.tech.fullstack.feature.about.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface About: NavKey

@Serializable
object AboutHome: About