package okik.tech.fullstack.data.repository.mappers

import okik.tech.fullstack.data.network.ApiResult
import okik.tech.fullstack.domain.DomainError
import okik.tech.fullstack.domain.DomainResult

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
