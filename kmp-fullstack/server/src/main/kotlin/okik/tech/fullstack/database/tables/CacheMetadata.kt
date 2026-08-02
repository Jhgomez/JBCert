package okik.tech.fullstack.database.tables

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.VarCharColumnType
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass


object CacheMetadataTable : IdTable<String>("cache_metadata") {
    val key = varchar("key", 100)
    val value = varchar("value", 255)
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(key)
    override val id: Column<EntityID<String>> = key.entityId()
}

class CacheMetadata(id: EntityID<String>) : Entity<String>(id) {
    companion object: EntityClass<String, CacheMetadata>(CacheMetadataTable)

    var key by CacheMetadataTable.key
    var value by CacheMetadataTable.value
    var updatedAt by CacheMetadataTable.updatedAt
}