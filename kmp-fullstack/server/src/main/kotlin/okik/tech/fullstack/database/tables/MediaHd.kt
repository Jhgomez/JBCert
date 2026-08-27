package okik.tech.fullstack.database.tables

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable

object MediaHd : IdTable<String>("media_hd") {
    override val id: Column<EntityID<String>> = varchar("hdurl", 256).entityId()
    override val primaryKey = PrimaryKey(id)

    val mediaFile = varchar("file", 256)
    val contentType = varchar("content_type", 64)
}

