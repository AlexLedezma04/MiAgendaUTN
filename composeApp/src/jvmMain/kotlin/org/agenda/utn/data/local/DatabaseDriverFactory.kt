package org.agenda.utn.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.agenda.utn.database.AgendaDatabase
import java.io.File

actual class DatabaseDriverFactory actual constructor() {

    actual fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), ".agenda")
        databasePath.mkdirs()

        val databaseFile = File(databasePath, "agenda.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")

        if (!databaseFile.exists()) {
            AgendaDatabase.Schema.create(driver)
        }

        return driver
    }
}
