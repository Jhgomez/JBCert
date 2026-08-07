package okik.tech.fullstack.network.restapi.util

import okik.tech.fullstack.network.client.NotFound
import okik.tech.fullstack.network.client.Timeout
import okik.tech.fullstack.network.client.Unauthorized
import okik.tech.fullstack.network.client.UnknownException
import okik.tech.fullstack.network.restapi.ApiResult

inline fun <T>safeRequest(block: () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(block())
    } catch (exception: NotFound) {
        ApiResult.NotFound(exception.reason)
    } catch (exception: Unauthorized) {
        ApiResult.Unauthorized(exception.reason)
    } catch (exception: Timeout) {
        ApiResult.TimeOut(exception.reason)
    } catch (exception: UnknownException) {
        ApiResult.UnknownResult(exception.reason)
    }
}