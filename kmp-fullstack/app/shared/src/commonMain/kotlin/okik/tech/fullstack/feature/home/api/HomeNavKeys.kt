package okik.tech.fullstack.feature.home.api

import androidx.navigation3.runtime.NavKey
import coil3.memory.MemoryCache
import kotlinx.serialization.Serializable
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.navigation.AppNavKey

@Serializable
sealed interface Home: AppNavKey

@Serializable
object HomeList: Home

@Serializable
class HomeApodDetail(
    val apod: Apod,
    val coilCacheKey: String?,
    val keyExtras: Map<String, String>?
): Home {
    override val shouldNavIcon: Boolean = true
}