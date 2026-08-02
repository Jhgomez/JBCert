package okik.tech.fullstack.database.tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.date

object CacheMetadataTable : Table("cache_metadata") {
    val key = varchar("key", 100).uniqueIndex()
    val value = varchar("value", 255)
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(key)
}

