package okik.tech.fullstack.database.tables

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.javatime.datetime

object CacheMetadata : IdTable<String>("cache_metadata") {

    override val id: Column<EntityID<String>> = varchar("key", 100).entityId()
    override val primaryKey = PrimaryKey(id)

    val value = varchar("value", 255).default("")
//    val updatedAt = datetime("updated_at")
    val updatedAt = long("updated_at")
}

