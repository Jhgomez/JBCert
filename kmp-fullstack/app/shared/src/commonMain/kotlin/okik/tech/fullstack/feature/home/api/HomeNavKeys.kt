package okik.tech.fullstack.feature.home.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import okik.tech.fullstack.domain.Apod

@Serializable
sealed interface Home: NavKey

@Serializable
object HomeList: Home

@Serializable
class HomeApodDetail(val apod: Apod?): Home