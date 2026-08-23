package okik.tech.fullstack.data.network

import okik.tech.fullstack.models.ErrorResponse

// this class should implement a class for whatever codes you expect to receive from your backend
sealed interface ApiResult<out T> {
    data class Success<R>(val result: R): ApiResult<R>

    sealed interface Error<V: Any>: ApiResult<V> {
        val errorResponse: ErrorResponse

        // any type of time out or network error which means user could retry
        data class NetworkError<R : Any>(override val errorResponse: ErrorResponse) : Error<R>

        data class NotFound<R : Any>(override val errorResponse: ErrorResponse) : Error<R>

        data class Unauthorized<R : Any>(override val errorResponse: ErrorResponse) : Error<R>

        // means any parsing error, or client misconfiguration we need to take care
        data class UnknownResult<R : Any>(override val errorResponse: ErrorResponse) : Error<R>

        data class UnhandledHttpCode<R : Any>(override val errorResponse: ErrorResponse) : Error<R>
    }
}