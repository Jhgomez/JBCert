package okik.tech.fullstack.data.repository.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.byUnicodePattern
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.models.ApodResponse
import kotlin.time.Instant

val dateFormat = LocalDate.Format {
    byUnicodePattern("yyyy-MM-dd")
}

fun Apod.toEntity() = ApodEntity(
    dateId = id,
    date = date,
    copyright = copyright,
    fetchedAt = fetchedAt.toEpochMilliseconds(),
    explanation = explanation,
    url = url,
    hdUrl = hdUrl,
    media_type = mediaType,
    title = title,
    thumbnailUrl = thumbnailUrl
)

fun ApodEntity.toDomainModel() = Apod(
    id = dateId,
    date = date,
    title = title,
    explanation = explanation,
    url = url,
    hdUrl = hdUrl,
    mediaType = media_type,
    copyright = copyright,
    thumbnailUrl = thumbnailUrl,
    fetchedAt = Instant.fromEpochMilliseconds(fetchedAt)
)

fun ApodResponse.toApodEntity() = ApodEntity(
    dateId = dateFormat.parse(date).toEpochDays(),
    date = date,
    copyright = copyright,
    fetchedAt = fetchedAt,
    explanation = explanation,
    // we do this because the project includes WEB targets so the backend is forced to implement CORS
    // and so is NASA's backend, CORS headers must come from the server that owns the resource. Web
    // browsers request an image directly from apod.nasa.gov(is COIL actually), and NASA doesn't send
    // "Access-Control-Allow-Origin",
    // A plain <img src="..."> wouldn't trigger this — images load cross-origin freely. This is called
    // preflight and means something is making(Coil) a non-simple request, i.e., fetching via JS, a
    // custom header, or crossOrigin set on the element, so the only way we can get access to this
    // resources is by proxying through our server.

    // An <iframe> (or <img>, <video>, <script>) displays a cross-origin resource. The browser loads
    // it, renders it, and your JavaScript never touches the bytes. No CORS involved, because there's
    // nothing to protect — you can't read the content programmatically. Coil does a fetch()/XHR which
    // reads the bytes into your code. That's where the same-origin policy applies, and that's why
    // it needs Access-Control-Allow-Origin. So embedding is allowed when your domain access other
    // domain but fetching requires CORS because it reads its bytes in its code

    // FYI, YouTube deliberately permits embedding — that's the entire product. NASA's image server
    // images are seem to be meant to be <img>-embedded, not fetched.
    url = "http://localhost:7070/api/apod/media?url=$url",
    hdUrl = hdUrl?.let { "http://localhost:7070/api/apod/media?url=$hdUrl&is_hd=true" },
    media_type = mediaType,
    title = title,
    thumbnailUrl = thumbnailUrl
)

fun ApodResponse.toDomainModel() = Apod(
    id = dateFormat.parse(date).toEpochDays(),
    date = date,
    title = title,
    explanation = explanation,
    url = "http://localhost:7070/api/apod/media?url=$url",
    hdUrl = hdUrl?.let { "http://localhost:7070/api/apod/media?url=$hdUrl&is_hd=true" },
    mediaType = mediaType,
    copyright = copyright,
    thumbnailUrl = thumbnailUrl,
    fetchedAt = Instant.fromEpochMilliseconds(fetchedAt)
)