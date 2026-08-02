package okik.tech.fullstack.database.tables

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.javatime.date
import java.time.LocalDate

object Apod: IdTable<LocalDate>("apod") {
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