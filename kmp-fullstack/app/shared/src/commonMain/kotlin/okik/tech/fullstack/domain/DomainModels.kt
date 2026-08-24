package okik.tech.fullstack.domain

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class Apod(
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdUrl: String? = null,
    val mediaType: String,
    val copyright: String? = null,
    val thumbnailUrl: String? = null,
    val fetchedAt: Instant = Clock.System.now()
)

data class PageInfo(
    val name: String,
    val page: Int
)

data class Paging<T>(
    val items: List<T>,
    val page: UByte,
    val pageSize: UByte,
    val totalItems: UShort,
    val totalPages: UByte
)

sealed interface DomainResult<out T> {
    data class Success<R>(val result: R): DomainResult<R>

    sealed class DomainErrorResult: DomainResult<Nothing>, Exception() {
        abstract val status: Int
        abstract override val message: String

        // any type of time out, or newtork error which means user could retry
        data class NetworkError(override val status: Int, override val message: String): DomainErrorResult()
        data class NotFound(override val status: Int, override val message: String): DomainErrorResult()
        data class Unauthorized(override val status: Int, override val message: String): DomainErrorResult()
        // any parsing error, or client misconfiguration we need to take care
        data class UnknownResult(override val status: Int, override val message: String): DomainErrorResult()
        data class UnhandledHttpCode(override val status: Int, override val message: String): DomainErrorResult()
    }
}