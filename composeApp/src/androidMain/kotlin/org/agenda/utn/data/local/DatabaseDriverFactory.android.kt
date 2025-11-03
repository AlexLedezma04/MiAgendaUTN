package org.agenda.utn.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.agenda.utn.database.AgendaDatabase

internal var appContext: Context? = null

actual class DatabaseDriverFactory actual constructor() {

    actual fun createDriver(): SqlDriver {
        val context = appContext ?: throw IllegalStateException(
            "Android context no inicializado. Llama a setAndroidContext primero."
        )

        return AndroidSqliteDriver(
            schema = AgendaDatabase.Schema,
            context = context,
            name = "agenda.db"
        )
    }
}