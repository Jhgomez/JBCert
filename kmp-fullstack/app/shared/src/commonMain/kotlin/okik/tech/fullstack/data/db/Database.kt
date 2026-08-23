package okik.tech.fullstack.data.db

import app.cash.sqldelight.db.SqlDriver
import okik.tech.fullstack.db.FullstackDb
import org.koin.dsl.module

interface DbDriverFactory {
    fun get(): SqlDriver
}

fun createDatabase(driverFactory: DbDriverFactory) : FullstackDb {
    return FullstackDb(driverFactory.get())
}