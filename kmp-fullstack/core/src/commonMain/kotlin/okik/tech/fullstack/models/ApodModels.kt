package okik.tech.fullstack.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class ApodResponse(
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    @SerialName("hdurl") val hdUrl: String? = null,
    @SerialName("media_type") val mediaType: String,
    val copyright: String? = null,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    val fetchedAt: String = Clock.System.now().toString()
)

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val page: UByte,
    val pageSize: UByte,
    val totalItems: UShort,
    val totalPages: UByte
)

@Serializable
data class ErrorResponse(
    val status: Int,
    val message: String
)