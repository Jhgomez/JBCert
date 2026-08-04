package okik.tech.fullstack.database.daos

import okik.tech.fullstack.database.tables.Apod
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.utils.dbQuery
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.upsert
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs
import kotlin.random.Random

class ApodDao {
    private val logger = LoggerFactory.getLogger(ApodDao::class.java)

    suspend fun getByDate(date: LocalDate): ApodResponse? = dbQuery {
        val apod = ApodEntity.Dao.findById(date)

            val result = apod?.let(ApodEntity::toApodResponse)

        result

    }

    suspend fun save(apod: ApodResponse): ApodResponse = dbQuery {
        val date = LocalDate.parse(apod.date)
        val exists = ApodEntity.Dao.findById(LocalDate.parse(apod.date)) != null

        Apod.upsert(Apod.id, onUpdateExclude = listOf(Apod.id, Apod.fetchedAt)) {
            it[id] = date
            it[copyright] = apod.copyright
            it[fetchedAt] = LocalDateTime.now(ZoneOffset.UTC)
            it[explanation] = apod.explanation
            it[url] = apod.url
            it[hdUrl] = apod.hdUrl
            it[media_type] = apod.mediaType
            it[title] = apod.title
            it[thumbnailUrl] = apod.thumbnailUrl

        }

        val formattedString = if (exists) "Cached apod entry with id/date ${apod.date} was updated" else
            "Caching new apod record with id/date ${apod.date}"

        logger.info(formattedString)

        apod
    }

    suspend fun getRandom(): ApodResponse? = dbQuery {
        val randomIndex = Random.nextLong(0, ApodEntity.Dao.count() - 1)

        ApodEntity.Dao.all()
            .offset(randomIndex)
            .limit(1)
            .firstOrNull()
            ?.let(ApodEntity::toApodResponse)
    }

    suspend fun getPaginated(page: UByte, pageSize: UByte): Pair<List<ApodResponse>, UShort> = dbQuery {
        val offset = abs(
            ((page - 1U) * pageSize).toLong()
        )

        val page = ApodEntity.Dao.all()
            .orderBy(Apod.id to SortOrder.ASC)
            .offset(offset)
            .limit(pageSize.toInt())
            .map(ApodEntity::toApodResponse)

        Pair(page, ApodEntity.Dao.count().toUShort())
    }

    suspend fun getWindowHistory(page: UByte, pageSize: UByte, star: LocalDate, end: LocalDate): List<ApodResponse> = dbQuery {
        val offset = abs(
            ((page - 1U) * pageSize).toLong()
        )

        ApodEntity.Dao
            .find { Apod.id greaterEq star and(Apod.id lessEq end) }
            .orderBy(Apod.id to SortOrder.ASC)
            .offset(offset)
            .limit(pageSize.toInt())
            .map(ApodEntity::toApodResponse)
    }

    suspend fun deleteOlderThan(cutoffDate: LocalDate): Int = dbQuery {
        val entries = ApodEntity.Dao.find { Apod.id less cutoffDate }
        val count = entries.count().toInt()

        entries.forEach { entry -> entry.delete() }

        count
    }

    suspend fun getTotalCount() = 
        dbQuery {
            ApodEntity.Dao.count()
        }
    

    suspend fun countInDateRange(startDate: LocalDate, endDate: LocalDate): Long = dbQuery {
            ApodEntity.Dao
                .find { Apod.id greaterEq startDate and (Apod.id lessEq endDate) }
                .count()
        }
}

/**
 *  using the DAO pattern https://www.jetbrains.com/help/exposed/get-started-with-exposed-dao.html
 *  however using this new DSL may not be that great since doesn't have the same capability as the
 *  classic DSL, for example it doesn't support "upsert"
 */
class ApodEntity(id: EntityID<LocalDate>) : Entity<LocalDate>(id) {

    object Dao : EntityClass<LocalDate, ApodEntity>(Apod)

    var date by Apod.id
    var copyright by Apod.copyright
    var fetchedAt by Apod.fetchedAt
    var explanation by Apod.explanation
    var url by Apod.url
    var hdUrl by Apod.hdUrl
    var media_type by Apod.media_type
    var title by Apod.title
    var thumbnailUrl by Apod.thumbnailUrl


    inline fun toApodResponse(): ApodResponse =
        ApodResponse(
            date = date.toString(), // no formatting as of right now, it will output iso format yyyy-MM-dd by default,
            title = title,
            explanation = explanation,
            url = url,
            hdUrl = hdUrl,
            mediaType = media_type,
            copyright = copyright,
            thumbnailUrl = thumbnailUrl,
            fetchedAt = fetchedAt.toInstant(ZoneOffset.UTC).toEpochMilli()
        )
}