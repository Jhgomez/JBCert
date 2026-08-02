package okik.tech.fullstack.database.daos

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.selects.whileSelect
import kotlinx.coroutines.withContext
import okik.tech.fullstack.database.tables.Apod
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.models.PaginatedResponse
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.upsert
import java.time.LocalDate
import kotlin.random.Random

/**
 *  using the DAO pattern https://www.jetbrains.com/help/exposed/get-started-with-exposed-dao.html
 *  however using this new DSL may not be that great since doesn't have the same capability as the
 *  classic DSL, for example it doesn't support "upsert"
 */
class ApodDao(id: EntityID<LocalDate>) : Entity<LocalDate>(id) {
    companion object : EntityClass<LocalDate, ApodDao>(Apod)
//    companion object : IntEntityClass<Task>(Tasks)

    var date by Apod.date
    var copyright by Apod.copyright
    var fetchedAt by Apod.fetchedAt
    var explanation by Apod.explanation
    var url by Apod.url
    var hdUrl by Apod.hdUrl
    var media_type by Apod.media_type
    var title by Apod.title
    var thumbnailUrl by Apod.thumbnailUrl


    override fun toString(): String {
        return "Apd(id=$id, title=$title)"
    }

    suspend fun getByDate(date: LocalDate): ApodResponse? =
        suspendTransaction {
            withContext(Dispatchers.IO) {
                findById(date)?.let { entry ->
                    ApodResponse(
                        date = entry.date.toString(), // no formatting as of right now, it will output iso format yyyy-MM-dd by default,
                        title = entry.title,
                        explanation = entry.explanation,
                        url = entry.url,
                        hdUrl = entry.hdUrl,
                        mediaType = entry.media_type,
                        copyright = entry.copyright,
                        thumbnailUrl = entry.thumbnailUrl,
                        fetchedAt = entry.fetchedAt
                    )
                }
            }
        }


    suspend fun save(apod: ApodResponse): ApodResponse =
        suspendTransaction {
            withContext(Dispatchers.IO) {
                val date = LocalDate.parse(apod.date)

                Apod.upsert(Apod.date) {
                    it[id] = date
                    it[copyright] = apod.copyright
                    it[fetchedAt] = apod.fetchedAt
                    it[explanation] = apod.explanation
                    it[url] = apod.url
                    it[hdUrl] = apod.hdUrl
                    it[media_type] = apod.mediaType
                    it[title] = apod.title
                    it[thumbnailUrl] = apod.thumbnailUrl

                }

                return@withContext apod
            }
        }

      suspend fun getRandom(): ApodResponse? =
          suspendTransaction {
              withContext(Dispatchers.IO) {
                  val randomIndex = Random.nextLong(0, count() - 1)

                  all()
                      .offset(randomIndex)
                      .limit(1)
                      .map(ApodDao::toApodResponse)
                      .firstOrNull()
              }
          }

    suspend fun getPaginated(page: UByte, pageSize: UByte): Pair<List<ApodResponse>, UShort> {
        val totalIndex = (page * pageSize).toUShort()

        val page = all()
            .orderBy(Apod.id to SortOrder.ASC)
            .offset((totalIndex - pageSize).toLong())
            .limit(pageSize.toInt())
            .map(ApodDao::toApodResponse)

        return Pair(page, count().toUShort())
    }

    suspend fun deleteOlderThan(cutoffDate: LocalDate): Int =
        suspendTransaction {
            withContext(Dispatchers.IO) {
                val entries = find { Apod.date less cutoffDate }
                val count = entries.count().toInt()

                entries.forEach { entry -> entry.delete() }

                count
            }
        }

    suspend fun getTotalCount() = suspendTransaction {
        withContext(Dispatchers.IO) {
            count()
        }
    }

    suspend fun countInDateRange(startDate: LocalDate, endDate: LocalDate): Long = suspendTransaction {
        withContext(Dispatchers.IO) {
            find { Apod.date greaterEq startDate and(Apod.date lessEq  endDate) }.count()
        }
    }

    private inline fun toApodResponse(): ApodResponse =
        ApodResponse(
            date = date.toString(), // no formatting as of right now, it will output iso format yyyy-MM-dd by default,
            title = title,
            explanation = explanation,
            url = url,
            hdUrl = hdUrl,
            mediaType = media_type,
            copyright = copyright,
            thumbnailUrl = thumbnailUrl,
            fetchedAt = fetchedAt
        )
}