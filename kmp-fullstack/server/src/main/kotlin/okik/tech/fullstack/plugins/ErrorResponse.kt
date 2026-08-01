package okik.tech.fullstack.plugins

@Serializable
data class ErrorResponse(
    val status: Int,
    val message: String
)