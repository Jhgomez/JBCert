package okik.tech.fullstack.domain

import kotlin.time.Clock

data class Apod(
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdUrl: String? = null,
    val mediaType: String,
    val copyright: String? = null,
    val thumbnailUrl: String? = null,
    val fetchedAt: String = Clock.System.now().toString()
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
    data class TimeOut<R>(val domainError: DomainError): DomainResult<R>
    data class NotFound<R>(val domainError: DomainError): DomainResult<R>
    data class Unauthorized<R>(val domainError: DomainError): DomainResult<R>
    data class UnknownResult<R>(val domainError: DomainError): DomainResult<R>
}