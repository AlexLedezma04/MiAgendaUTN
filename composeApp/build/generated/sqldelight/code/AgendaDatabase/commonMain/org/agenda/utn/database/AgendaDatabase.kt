package org.agenda.utn.database

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import kotlin.Unit
import org.agenda.utn.database.composeApp.newInstance
import org.agenda.utn.database.composeApp.schema
import orgagendautndatabase.TaskQueries

public interface AgendaDatabase : Transacter {
  public val taskQueries: TaskQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = AgendaDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): AgendaDatabase =
        AgendaDatabase::class.newInstance(driver)
  }
}
