package okik.tech.fullstack.data.network.restapi.util

import okik.tech.fullstack.data.network.ApiResult
import okik.tech.fullstack.data.network.client.NotFound
import okik.tech.fullstack.data.network.client.NetworkError
import okik.tech.fullstack.data.network.client.Unauthorized
import okik.tech.fullstack.data.network.client.UnhandledHttpCode
import okik.tech.fullstack.data.network.client.UnknownException

inline fun <T>safeRequest(block: () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(block())
    } catch (exception: NotFound) {
        ApiResult.Error.NotFound(exception.reason)
    } catch (exception: Unauthorized) {
        ApiResult.Error.Unauthorized(exception.reason)
    } catch (exception: NetworkError) {
        ApiResult.Error.NetworkError(exception.reason)
    } catch (exception: UnknownException) {
        ApiResult.Error.UnknownResult(exception.reason)
    } catch (exception: UnhandledHttpCode) {
        ApiResult.Error.UnhandledHttpCode(exception.reason)
    }
}