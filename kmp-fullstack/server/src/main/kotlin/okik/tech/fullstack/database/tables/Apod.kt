package okik.tech.fullstack.database.tables

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ColumnType
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.javatime.Date
import org.jetbrains.exposed.v1.javatime.date
import java.time.LocalDate

private object ApodTable: IdTable<LocalDate>("apod") {
    val date = date("date").uniqueIndex()
    val copyright = varchar("copyright", 255).nullable()
    val fetchedAt = long("fetched_at").default(0)
    val explanation = text("explanation")
    val url = varchar("url", 500)
    val hdUrl = varchar("hd_url", 500).nullable()
    val media_type = varchar("media_type", 20)
    val title = varchar("title", 255)
    val thumbnailUrl = varchar("thumbnail_url", 500).nullable()

    override val primaryKey = PrimaryKey(date)
    override val id: Column<EntityID<LocalDate>>
        get() = date.entityId()
}

/**
 *  using the DAO pattern https://www.jetbrains.com/help/exposed/get-started-with-exposed-dao.html
  */
class Apod(id: EntityID<LocalDate>) : Entity<LocalDate>(id) {
    companion object : EntityClass<LocalDate, Apod>(ApodTable)
//    companion object : IntEntityClass<Task>(Tasks)

    var date by ApodTable.date
    var copyright by ApodTable.copyright
    var fetchedAt by ApodTable.fetchedAt
    var explanation by ApodTable.explanation
    var url by ApodTable.url
    var hdUrl by ApodTable.hdUrl
    var media_type by ApodTable.media_type
    var title by ApodTable.title
    var thumbnailUrl by ApodTable.thumbnailUrl


    override fun toString(): String {
        return "Apd(id=$id, title=$title)"
    }
}