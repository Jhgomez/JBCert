package okik.tech.fullstack.feature.search.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import okik.tech.fullstack.domain.Apod

@Serializable
sealed interface Search: NavKey

@Serializable
object SearchHome: Search

@Serializable
class SearchDetail(val apod: Apod?): Search