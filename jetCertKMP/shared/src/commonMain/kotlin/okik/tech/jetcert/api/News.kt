package okik.tech.jetcert.api

import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.ContentConverter
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

//@Serializable(with = NewsResponseSerializer::class)
@Serializable(with = NewsResponse.NewResponseSerializer::class)
sealed interface NewsResponse {

    @Serializable
    @SerialName("story")
    data class Story(
        val id: Long,
        val type: String,
        val time: Long,
        val by: String? = null,
        val title: String? = null,
        val score: Int? = null,
        val url: String? = null,
        val descendants: Int? = null,
        val kids: List<Long>? = null,
        val text: String? = null,
    ) : NewsResponse

    // this one is used because the API we are going to use returns a polymorphic json, so it can
    // return different type of objects, we will only accept a story object all other objects will be parsed as
    // null response
    @Serializable
    data object NullResponse: NewsResponse

    private object NewResponseSerializer:
        JsonContentPolymorphicSerializer<NewsResponse>(NewsResponse::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<NewsResponse> = when {
            element is JsonNull -> NullResponse.serializer()
            else -> Story.serializer()
        }

    }
}