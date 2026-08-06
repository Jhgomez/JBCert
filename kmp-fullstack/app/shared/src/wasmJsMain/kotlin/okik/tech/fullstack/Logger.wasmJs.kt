package okik.tech.fullstack

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun($$"(tag, message) => console.info(`${tag} - ${message}`)")
external fun wasmLogInfo(tag: String, message: String)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun($$"(tag, message) => console.error(`${tag} - ${message}`)")
external fun wasmLogError(tag: String, message: String)

actual object Logger {

    actual fun logInfo(tag: String, message: String) {
        wasmLogInfo(tag, message)
    }

    actual fun logError(tag: String, message: String) {
        wasmLogError(tag, message)
    }
}