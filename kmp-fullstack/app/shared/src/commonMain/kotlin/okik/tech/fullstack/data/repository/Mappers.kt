package okik.tech.fullstack.data.repository

import okik.tech.fullstack.data.network.ApiResult
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.DomainError
import okik.tech.fullstack.domain.DomainResult

import okik.tech.fullstack.domain.Paging
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.models.ErrorResponse
import okik.tech.fullstack.models.PaginatedResponse

fun <T, R> PaginatedResponse<T>.toDomainModel(mapper: (T) -> R) = Paging(
    items = items.map(mapper),
    page = page,
    pageSize = pageSize,
    totalItems = totalItems,
    totalPages = totalPages
)

fun ApodResponse.toDomainModel() = Apod(
    date = date,
    title = title,
    explanation = explanation,
    url = url,
    hdUrl = hdUrl,
    mediaType = mediaType,
    copyright = copyright,
    thumbnailUrl = thumbnailUrl,
    fetchedAt = fetchedAt
)

fun ErrorResponse.toDomainModel() = DomainError(
    status = status,
    message = message
)

fun <T, R> ApiResult<T>.toDomainModel(successMapper: (dataModel: T) -> R): DomainResult<R> = when(this) {
    is ApiResult.NotFound<T> -> DomainResult.DomainErrorResult.NotFound(errorResponse.toDomainModel())
    is ApiResult.Success<T> -> DomainResult.Success(successMapper.invoke(this.result))
    is ApiResult.NetworkError<T> -> DomainResult.DomainErrorResult.NetworkError(errorResponse.toDomainModel())
    is ApiResult.Unauthorized<T> -> DomainResult.DomainErrorResult.Unauthorized(errorResponse.toDomainModel())
    is ApiResult.UnknownResult<T> -> DomainResult.DomainErrorResult.UnknownResult(errorResponse.toDomainModel())
    is ApiResult.UnhandledHttpCode<*> -> DomainResult.DomainErrorResult.UnhandledHttpCode(errorResponse.toDomainModel())
}