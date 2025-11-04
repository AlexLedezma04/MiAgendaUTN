package orgagendautndatabase

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Boolean
import kotlin.Long
import kotlin.String

public class TaskQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> getAllTasks(mapper: (
    id: Long,
    title: String,
    description: String,
    dueDate: Long,
    category: String,
    isCompleted: Boolean,
    createdAt: Long,
    updatedAt: Long,
  ) -> T): Query<T> = Query(267_970_288, arrayOf("Tasks"), driver, "Task.sq", "getAllTasks", """
  |SELECT Tasks.id, Tasks.title, Tasks.description, Tasks.dueDate, Tasks.category, Tasks.isCompleted, Tasks.createdAt, Tasks.updatedAt FROM Tasks
  |ORDER BY dueDate ASC
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getString(4)!!,
      cursor.getBoolean(5)!!,
      cursor.getLong(6)!!,
      cursor.getLong(7)!!
    )
  }

  public fun getAllTasks(): Query<Tasks> = getAllTasks { id, title, description, dueDate, category,
      isCompleted, createdAt, updatedAt ->
    Tasks(
      id,
      title,
      description,
      dueDate,
      category,
      isCompleted,
      createdAt,
      updatedAt
    )
  }

  public fun <T : Any> getTasksByStatus(isCompleted: Boolean, mapper: (
    id: Long,
    title: String,
    description: String,
    dueDate: Long,
    category: String,
    isCompleted: Boolean,
    createdAt: Long,
    updatedAt: Long,
  ) -> T): Query<T> = GetTasksByStatusQuery(isCompleted) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getString(4)!!,
      cursor.getBoolean(5)!!,
      cursor.getLong(6)!!,
      cursor.getLong(7)!!
    )
  }

  public fun getTasksByStatus(isCompleted: Boolean): Query<Tasks> = getTasksByStatus(isCompleted) {
      id, title, description, dueDate, category, isCompleted_, createdAt, updatedAt ->
    Tasks(
      id,
      title,
      description,
      dueDate,
      category,
      isCompleted_,
      createdAt,
      updatedAt
    )
  }

  public fun <T : Any> getTasksByCategory(category: String, mapper: (
    id: Long,
    title: String,
    description: String,
    dueDate: Long,
    category: String,
    isCompleted: Boolean,
    createdAt: Long,
    updatedAt: Long,
  ) -> T): Query<T> = GetTasksByCategoryQuery(category) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getString(4)!!,
      cursor.getBoolean(5)!!,
      cursor.getLong(6)!!,
      cursor.getLong(7)!!
    )
  }

  public fun getTasksByCategory(category: String): Query<Tasks> = getTasksByCategory(category) { id,
      title, description, dueDate, category_, isCompleted, createdAt, updatedAt ->
    Tasks(
      id,
      title,
      description,
      dueDate,
      category_,
      isCompleted,
      createdAt,
      updatedAt
    )
  }

  public fun <T : Any> getTasksByStatusAndCategory(
    isCompleted: Boolean,
    category: String,
    mapper: (
      id: Long,
      title: String,
      description: String,
      dueDate: Long,
      category: String,
      isCompleted: Boolean,
      createdAt: Long,
      updatedAt: Long,
    ) -> T,
  ): Query<T> = GetTasksByStatusAndCategoryQuery(isCompleted, category) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getString(4)!!,
      cursor.getBoolean(5)!!,
      cursor.getLong(6)!!,
      cursor.getLong(7)!!
    )
  }

  public fun getTasksByStatusAndCategory(isCompleted: Boolean, category: String): Query<Tasks> =
      getTasksByStatusAndCategory(isCompleted, category) { id, title, description, dueDate,
      category_, isCompleted_, createdAt, updatedAt ->
    Tasks(
      id,
      title,
      description,
      dueDate,
      category_,
      isCompleted_,
      createdAt,
      updatedAt
    )
  }

  public fun getAllCategories(): Query<String> = Query(581_216_250, arrayOf("Tasks"), driver,
      "Task.sq", "getAllCategories", """
  |SELECT DISTINCT category FROM Tasks
  |ORDER BY category ASC
  """.trimMargin()) { cursor ->
    cursor.getString(0)!!
  }

  public fun <T : Any> getTaskById(id: Long, mapper: (
    id: Long,
    title: String,
    description: String,
    dueDate: Long,
    category: String,
    isCompleted: Boolean,
    createdAt: Long,
    updatedAt: Long,
  ) -> T): Query<T> = GetTaskByIdQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getString(4)!!,
      cursor.getBoolean(5)!!,
      cursor.getLong(6)!!,
      cursor.getLong(7)!!
    )
  }

  public fun getTaskById(id: Long): Query<Tasks> = getTaskById(id) { id_, title, description,
      dueDate, category, isCompleted, createdAt, updatedAt ->
    Tasks(
      id_,
      title,
      description,
      dueDate,
      category,
      isCompleted,
      createdAt,
      updatedAt
    )
  }

  public fun countTasksByStatus(isCompleted: Boolean): Query<Long> =
      CountTasksByStatusQuery(isCompleted) { cursor ->
    cursor.getLong(0)!!
  }

  public fun countAllTasks(): Query<Long> = Query(1_140_805_737, arrayOf("Tasks"), driver,
      "Task.sq", "countAllTasks", "SELECT COUNT(*) FROM Tasks") { cursor ->
    cursor.getLong(0)!!
  }

  /**
   * @return The number of rows updated.
   */
  public fun insertTask(
    title: String,
    description: String,
    dueDate: Long,
    category: String,
    isCompleted: Boolean,
    createdAt: Long,
    updatedAt: Long,
  ): QueryResult<Long> {
    val result = driver.execute(711_563_057, """
        |INSERT INTO Tasks (title, description, dueDate, category, isCompleted, createdAt, updatedAt)
        |VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 7) {
          bindString(0, title)
          bindString(1, description)
          bindLong(2, dueDate)
          bindString(3, category)
          bindBoolean(4, isCompleted)
          bindLong(5, createdAt)
          bindLong(6, updatedAt)
        }
    notifyQueries(711_563_057) { emit ->
      emit("Tasks")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun updateTask(
    title: String,
    description: String,
    dueDate: Long,
    category: String,
    isCompleted: Boolean,
    updatedAt: Long,
    id: Long,
  ): QueryResult<Long> {
    val result = driver.execute(-550_533_823, """
        |UPDATE Tasks
        |SET title = ?, description = ?, dueDate = ?, category = ?, isCompleted = ?, updatedAt = ?
        |WHERE id = ?
        """.trimMargin(), 7) {
          bindString(0, title)
          bindString(1, description)
          bindLong(2, dueDate)
          bindString(3, category)
          bindBoolean(4, isCompleted)
          bindLong(5, updatedAt)
          bindLong(6, id)
        }
    notifyQueries(-550_533_823) { emit ->
      emit("Tasks")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun markTaskAsCompleted(updatedAt: Long, id: Long): QueryResult<Long> {
    val result = driver.execute(1_486_281_460, """
        |UPDATE Tasks
        |SET isCompleted = 1, updatedAt = ?
        |WHERE id = ?
        """.trimMargin(), 2) {
          bindLong(0, updatedAt)
          bindLong(1, id)
        }
    notifyQueries(1_486_281_460) { emit ->
      emit("Tasks")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun deleteTask(id: Long): QueryResult<Long> {
    val result = driver.execute(1_509_986_595, """
        |DELETE FROM Tasks
        |WHERE id = ?
        """.trimMargin(), 1) {
          bindLong(0, id)
        }
    notifyQueries(1_509_986_595) { emit ->
      emit("Tasks")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun deleteAllTasks(): QueryResult<Long> {
    val result = driver.execute(-631_026_933, """DELETE FROM Tasks""", 0)
    notifyQueries(-631_026_933) { emit ->
      emit("Tasks")
    }
    return result
  }

  private inner class GetTasksByStatusQuery<out T : Any>(
    public val isCompleted: Boolean,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Tasks", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Tasks", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-496_615_884, """
    |SELECT Tasks.id, Tasks.title, Tasks.description, Tasks.dueDate, Tasks.category, Tasks.isCompleted, Tasks.createdAt, Tasks.updatedAt FROM Tasks
    |WHERE isCompleted = ?
    |ORDER BY dueDate ASC
    """.trimMargin(), mapper, 1) {
      bindBoolean(0, isCompleted)
    }

    override fun toString(): String = "Task.sq:getTasksByStatus"
  }

  private inner class GetTasksByCategoryQuery<out T : Any>(
    public val category: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Tasks", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Tasks", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_774_673_216, """
    |SELECT Tasks.id, Tasks.title, Tasks.description, Tasks.dueDate, Tasks.category, Tasks.isCompleted, Tasks.createdAt, Tasks.updatedAt FROM Tasks
    |WHERE category = ?
    |ORDER BY dueDate ASC
    """.trimMargin(), mapper, 1) {
      bindString(0, category)
    }

    override fun toString(): String = "Task.sq:getTasksByCategory"
  }

  private inner class GetTasksByStatusAndCategoryQuery<out T : Any>(
    public val isCompleted: Boolean,
    public val category: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Tasks", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Tasks", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(255_137_057, """
    |SELECT Tasks.id, Tasks.title, Tasks.description, Tasks.dueDate, Tasks.category, Tasks.isCompleted, Tasks.createdAt, Tasks.updatedAt FROM Tasks
    |WHERE isCompleted = ? AND category = ?
    |ORDER BY dueDate ASC
    """.trimMargin(), mapper, 2) {
      bindBoolean(0, isCompleted)
      bindString(1, category)
    }

    override fun toString(): String = "Task.sq:getTasksByStatusAndCategory"
  }

  private inner class GetTaskByIdQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Tasks", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Tasks", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_930_251_398, """
    |SELECT Tasks.id, Tasks.title, Tasks.description, Tasks.dueDate, Tasks.category, Tasks.isCompleted, Tasks.createdAt, Tasks.updatedAt FROM Tasks
    |WHERE id = ?
    """.trimMargin(), mapper, 1) {
      bindLong(0, id)
    }

    override fun toString(): String = "Task.sq:getTaskById"
  }

  private inner class CountTasksByStatusQuery<out T : Any>(
    public val isCompleted: Boolean,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("Tasks", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("Tasks", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_031_002_203, """
    |SELECT COUNT(*) FROM Tasks
    |WHERE isCompleted = ?
    """.trimMargin(), mapper, 1) {
      bindBoolean(0, isCompleted)
    }

    override fun toString(): String = "Task.sq:countTasksByStatus"
  }
}
