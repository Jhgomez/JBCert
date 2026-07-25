package okik.tech.jetcert.db

import app.cash.sqldelight.db.SqlDriver

expect fun createDriver(): SqlDriver

fun createDataBase(): JetcertDB = JetcertDB(createDriver())