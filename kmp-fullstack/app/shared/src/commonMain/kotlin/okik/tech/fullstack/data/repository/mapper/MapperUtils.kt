package okik.tech.fullstack.data.repository.mapper

import okik.tech.fullstack.data.network.ApiResult
import okik.tech.fullstack.domain.DomainResult

fun <T : Any> ApiResult.Error<T>.toDomainResultError(): DomainResult.DomainErrorResult =
    when(this) {
        is ApiResult.Error.NotFound<T> -> 
            DomainResult.DomainErrorResult.NotFound(errorResponse.status, errorResponse.message)
        is ApiResult.Error.NetworkError<T> -> 
            DomainResult.DomainErrorResult.NetworkError(errorResponse.status, errorResponse.message)
        is ApiResult.Error.Unauthorized<T> ->
            DomainResult.DomainErrorResult.Unauthorized(errorResponse.status, errorResponse.message)
        is ApiResult.Error.UnknownResult<T> -> 
            DomainResult.DomainErrorResult.UnknownResult(errorResponse.status, errorResponse.message)
        is ApiResult.Error.UnhandledHttpCode<*> -> 
            DomainResult.DomainErrorResult.UnhandledHttpCode(errorResponse.status, errorResponse.message)
    }
