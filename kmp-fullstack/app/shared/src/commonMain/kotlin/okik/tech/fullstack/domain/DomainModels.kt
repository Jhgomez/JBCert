package okik.tech.fullstack.domain

import kotlin.time.Clock
import kotlin.time.Instant

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

data class DomainError(
    val status: Int,
    val message: String
)

sealed interface DomainResult<out T> {
    data class Success<R>(val result: R): DomainResult<R>

    sealed interface DomainErrorResult<R>: DomainResult<R> {
        val domainError: DomainError

        // any type of time out, or newtork error which means user could retry
        data class NetworkError<M>(override val domainError: DomainError): DomainErrorResult<M>
        data class NotFound<M>(override val domainError: DomainError): DomainErrorResult<M>
        data class Unauthorized<M>(override val domainError: DomainError): DomainErrorResult<M>
        // any parsing error, or client misconfiguration we need to take care
        data class UnknownResult<M>(override val domainError: DomainError): DomainErrorResult<M>
        data class UnhandledHttpCode<M>(override val domainError: DomainError): DomainErrorResult<M>
    }
}