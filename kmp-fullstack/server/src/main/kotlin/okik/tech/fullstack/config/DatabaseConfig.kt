package okik.tech.fullstack.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import okik.tech.fullstack.database.daos.ApodDao
import okik.tech.fullstack.database.daos.CacheMetadataDao
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import okik.tech.fullstack.database.tables.Apod
import okik.tech.fullstack.database.tables.CacheMetadata
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseConfig {
    val databaseModule = module {
        single {
            val appConfig = get<AppConfig>()
            createHikariDataSource(appConfig.dbFilePath)
        }
        single {
            val dataSource = get<HikariDataSource>()
            createDatabase(dataSource)
        }

        single { ApodDao() }

        single { CacheMetadataDao() }

    }

    private fun createHikariDataSource(dbFilePath: String): HikariDataSource {
//        File(dbFilePath).parentFile?.mkdirs()

        val config = HikariConfig().apply {
            driverClassName = "org.sqlite.JDBC"
//            jdbcUrl = "jdbc:sqlite:$dbFilePath"
            jdbcUrl = "jdbc:sqlite:apod.db"
            username = "user"
            password = "password"
            maximumPoolSize = 5
            minimumIdle = 2
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"

            dataSourceProperties["journal_mode"] = "WAL"
            dataSourceProperties["synchronous"] = "NORMAL"

            connectionTestQuery = "SELECT 1"
            validationTimeout = 3000
        }

        return HikariDataSource(config)
    }

    private fun createDatabase(dataSource: HikariDataSource): Database {
        return Database.connect(dataSource)
    }
}

fun Application.initializeDatabase() {
    val database by inject<Database>()
    val appConfig by inject<AppConfig>()

    transaction(database) {
        SchemaUtils.create(Apod, CacheMetadata)
    }

    log.info("SQLite database initialized successfully at: ${appConfig.dbFilePath}")
}