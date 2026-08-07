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
        ApiResult.NotFound(exception.reason)
    } catch (exception: Unauthorized) {
        ApiResult.Unauthorized(exception.reason)
    } catch (exception: NetworkError) {
        ApiResult.NetworkError(exception.reason)
    } catch (exception: UnknownException) {
        ApiResult.UnknownResult(exception.reason)
    } catch (exception: UnhandledHttpCode) {
        ApiResult.UnhandledHttpCode(exception.reason)
    }
}