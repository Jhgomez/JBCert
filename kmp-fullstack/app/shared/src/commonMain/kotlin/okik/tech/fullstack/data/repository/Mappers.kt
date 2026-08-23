package okik.tech.fullstack.data.repository

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.byUnicodePattern
import okik.tech.fullstack.data.network.ApiResult
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.DomainError
import okik.tech.fullstack.domain.DomainResult

import okik.tech.fullstack.domain.Paging
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.models.PaginatedResponse
import kotlin.time.Instant

fun <T, R> PaginatedResponse<T>.toDomainErrorModel(mapper: (T) -> R) = Paging(
    items = items.map(mapper),
    page = page,
    pageSize = pageSize,
    totalItems = totalItems,
    totalPages = totalPages
)

fun ApodEntity.toDomainModel() = Apod(
    date = LocalDate.fromEpochDays(dateId).toString(),
    title = title,
    explanation = explanation,
    url = url,
    hdUrl = hdUrl,
    mediaType = media_type,
    copyright = copyright,
    thumbnailUrl = thumbnailUrl,
    fetchedAt = Instant.fromEpochMilliseconds(fetchedAt).toString()
)

val dateFormat = LocalDate.Format {
    byUnicodePattern("yyyy-MM-dd")
}

fun ApodResponse.toApodEntity() = ApodEntity(
    dateId = dateFormat.parse(date).toEpochDays(),
    copyright = copyright,
    fetchedAt = fetchedAt,
    explanation = explanation,
    url = url,
    hdUrl = hdUrl,
    media_type = mediaType,
    title = title,
    thumbnailUrl = thumbnailUrl
)

fun <T: Any> ApiResult.Error<T>.toDomainError() = DomainError(
    status = errorResponse.status,
    message = errorResponse.message
)

fun <T : Any, R> ApiResult.Error<T>.toDomainResultError(): DomainResult.DomainErrorResult<R> =
    when(this) {
        is ApiResult.Error.NotFound<T> -> 
            DomainResult.DomainErrorResult.NotFound(toDomainError())
        is ApiResult.Error.NetworkError<T> -> 
            DomainResult.DomainErrorResult.NetworkError(toDomainError())
        is ApiResult.Error.Unauthorized<T> ->
            DomainResult.DomainErrorResult.Unauthorized(toDomainError())
        is ApiResult.Error.UnknownResult<T> -> 
            DomainResult.DomainErrorResult.UnknownResult(toDomainError())
        is ApiResult.Error.UnhandledHttpCode<*> -> 
            DomainResult.DomainErrorResult.UnhandledHttpCode(toDomainError())
    }
