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
    url = url,
    hdUrl = hdUrl,
    media_type = mediaType,
    title = title,
    thumbnailUrl = thumbnailUrl
)

fun ApodResponse.toDomainModel() = Apod(
    id = dateFormat.parse(date).toEpochDays(),
    date = date,
    title = title,
    explanation = explanation,
    url = url,
    hdUrl = hdUrl,
    mediaType = mediaType,
    copyright = copyright,
    thumbnailUrl = thumbnailUrl,
    fetchedAt = Instant.fromEpochMilliseconds(fetchedAt)
)