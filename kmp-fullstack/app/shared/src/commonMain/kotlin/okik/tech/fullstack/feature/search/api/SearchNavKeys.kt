package okik.tech.fullstack.feature.search.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.navigation.AppNavKey

@Serializable
sealed interface Search: AppNavKey

@Serializable
object SearchHome: Search
