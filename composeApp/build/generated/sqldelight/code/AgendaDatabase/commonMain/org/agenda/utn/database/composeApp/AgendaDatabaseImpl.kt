package org.agenda.utn.database.composeApp

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass
import org.agenda.utn.database.AgendaDatabase
import orgagendautndatabase.TaskQueries

internal val KClass<AgendaDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = AgendaDatabaseImpl.Schema

internal fun KClass<AgendaDatabase>.newInstance(driver: SqlDriver): AgendaDatabase =
    AgendaDatabaseImpl(driver)

private class AgendaDatabaseImpl(
  driver: SqlDriver,
) : TransacterImpl(driver),
    AgendaDatabase {
  override val taskQueries: TaskQueries = TaskQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE Tasks (
          |    id INTEGER PRIMARY KEY AUTOINCREMENT,
          |    title TEXT NOT NULL,
          |    description TEXT NOT NULL,
          |    dueDate INTEGER NOT NULL,
          |    category TEXT NOT NULL,
          |    isCompleted INTEGER NOT NULL DEFAULT 0,
          |    createdAt INTEGER NOT NULL,
          |    updatedAt INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit
  }
}
