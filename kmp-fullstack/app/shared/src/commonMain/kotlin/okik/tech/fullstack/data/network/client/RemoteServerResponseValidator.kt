package okik.tech.fullstack.data.network.client

import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import okik.tech.fullstack.models.ErrorResponse
import kotlin.Exception


// Exactly how this is handled really depends on the client library you're using, but what should
// not change is the backend behavior and best practices, that is, the behavior is that all http
// responses in someway are successful but only partially because, all could be considered successful
// partially because they all traveled to the server and the server ws able to respond, but not all
// are fully successful because only responses with 2xx status codes are considered successful. The
// best practice is that the backend should return a consistent object(represented in json or whatever
// other text format we are using, XML, YAML, CSV) in all reponses with "not successful" http status
// codes. Regurlarly the client would want to decode that error message into an object which is what
// we are doing here("exceptionResponse.body<ErrorResponse>()"), and sucessfull responses will just
// continue to travel, delivered, to whoever is making the request which would usually be a "service"
// clase, that class will decode its body into whatever object it expects. One way to order this flow
// would be to wrap our rest client into another class that act as a some sort of adapter which would
// have similar calls to our rest client but will return a Sucess or failure, it would "centralize"
// status code checking, that class is what we would then inject into our services, but that would
// just create one more class, and also more classes to wrap the bodies of the responses for
// something that we already can handle with this exceptions approach, in this approach we can
// centralize the error handling which is how we are going to communicate the result to the next layer
// we catch excepntions and turn them into proper models.  We could remove this validator and catch
// the exceptions in the http call site but Ktor is already handling concurrency here, for example
// "exceptionResponse.body" is suspend so we dont suspend the wrong coroutine we trust Ktor
fun HttpClientConfig<*>.configureHttpResponseValidator() {
    // this will make the client to throw exceptions if response doesn't have a 2xx http status code
    expectSuccess = true

    // when the error hits we handle it here
    HttpResponseValidator {

        // "exception.response" is the response of any response sent by the server that has a
        // non 2xx http status code, since we are sharing the models in the core module, we already
        // know its format
        handleResponseExceptionWithRequest { exception, request ->
            throw when (exception) {
                is ClientRequestException, is RedirectResponseException, is ServerResponseException -> {
                    val exceptionResponse = exception.response
                    val exceptionResponseText = exceptionResponse.body<ErrorResponse>()

                    // Ktor client will throw timeout exceptions, in which case we can
                    // offer the user to retry
                    when (exceptionResponse.status) {
                        HttpStatusCode.NotFound -> NotFound(exceptionResponseText)
                        HttpStatusCode.Unauthorized -> Unauthorized(exceptionResponseText)
                        else ->  UnhandledHttpCode(exceptionResponseText)
                    }
                }
                is IOException -> NetworkError(ErrorResponse(
                        0,
                        exception.message ?: ""
                    )
                )
                else -> UnknownException(ErrorResponse(0, exception.message ?: ""))
            }
        }
    }
}

data class UnhandledHttpCode(val reason: ErrorResponse): Exception()
data class NotFound(val reason: ErrorResponse): Exception()
data class Unauthorized(val reason: ErrorResponse): Exception()

// any type of time out, or newtork error which means user could retry
data class NetworkError(val reason: ErrorResponse): Exception()
// any parsing error, or client misconfiguration we need to take care
data class UnknownException(val reason: ErrorResponse): Exception()