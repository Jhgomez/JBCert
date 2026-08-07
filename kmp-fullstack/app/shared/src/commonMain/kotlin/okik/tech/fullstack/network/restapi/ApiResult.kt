package okik.tech.fullstack.network.restapi

import okik.tech.fullstack.models.ErrorResponse

// this class should implement a class for whatever codes you expect to receive from your backend
sealed interface ApiResult<out T> {
    data class Success<R>(val result: R): ApiResult<R>
    data class NotFound<R>(val errorResponse: ErrorResponse): ApiResult<R>
    data class Unauthorized<R>(val errorResponse: ErrorResponse): ApiResult<R>
    data class TimeOut<R>(val errorResponse: ErrorResponse): ApiResult<R>
    data class UnknownResult<R>(val errorResponse: ErrorResponse): ApiResult<R>
}