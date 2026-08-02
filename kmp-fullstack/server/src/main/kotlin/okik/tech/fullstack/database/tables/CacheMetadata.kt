package okik.tech.fullstack.database.tables

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable

object CacheMetadata : IdTable<String>("cache_metadata") {
    val key = varchar("key", 100)
    val value = varchar("value", 255)
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(key)
    override val id: Column<EntityID<String>> = key.entityId()
}

