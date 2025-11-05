package org.agenda.utn.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.agenda.utn.database.AgendaDatabase
import orgagendautndatabase.Tasks
import org.agenda.utn.domain.model.Task
import org.agenda.utn.domain.model.TaskFilter

class TaskRepository(database: AgendaDatabase) {

    private val queries = database.taskQueries

    fun getAllTasksFlow(): Flow<List<Task>> {
        return queries.getAllTasks()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    fun getTasksByFilter(filter: TaskFilter, category: String? = null): Flow<List<Task>> {
        return when {
            // Filtrar por estado y categoría
            category != null && filter != TaskFilter.ALL -> {
                queries.getTasksByStatusAndCategory(
                    isCompleted = filter == TaskFilter.COMPLETED,
                    category = category
                ).asFlow()
                    .mapToList(Dispatchers.Default)
                    .map { entities -> entities.map { it.toDomainModel() } }
            }
            // Filtrar solo por categoría
            category != null -> {
                queries.getTasksByCategory(category)
                    .asFlow()
                    .mapToList(Dispatchers.Default)
                    .map { entities -> entities.map { it.toDomainModel() } }
            }
            // Filtrar solo por estado
            filter != TaskFilter.ALL -> {
                queries.getTasksByStatus(filter == TaskFilter.COMPLETED)
                    .asFlow()
                    .mapToList(Dispatchers.Default)
                    .map { entities -> entities.map { it.toDomainModel() } }
            }
            // Sin filtros
            else -> getAllTasksFlow()
        }
    }

    suspend fun getTaskById(id: Long): Task? = withContext(Dispatchers.Default) {
        try {
            queries.getTaskById(id).executeAsOneOrNull()?.toDomainModel()
        } catch (e: Exception) {
            println("Error al obtener tarea por ID: ${e.message}")
            null
        }
    }

    fun getAllCategoriesFlow(): Flow<List<String>> {
        return queries.getAllCategories()
            .asFlow()
            .mapToList(Dispatchers.Default)
    }

    suspend fun insertTask(task: Task): Result<Long> = withContext(Dispatchers.Default) {
        try {
            queries.insertTask(
                title = task.title,
                description = task.description,
                dueDate = task.dueDate,
                category = task.category,
                isCompleted = task.isCompleted,
                createdAt = task.createdAt,
                updatedAt = task.updatedAt
            )

            // Obtener el ID de la última inserción
            val lastId = queries.countAllTasks().executeAsOne()
            Result.success(lastId)
        } catch (e: Exception) {
            println("Error al insertar tarea: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            queries.updateTask(
                title = task.title,
                description = task.description,
                dueDate = task.dueDate,
                category = task.category,
                isCompleted = task.isCompleted,
                updatedAt = System.currentTimeMillis(),
                id = task.id
            )
            Result.success(Unit)
        } catch (e: Exception) {
            println("Error al actualizar tarea: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun markTaskAsCompleted(taskId: Long): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            queries.markTaskAsCompleted(
                updatedAt = System.currentTimeMillis(),
                id = taskId
            )
            Result.success(Unit)
        } catch (e: Exception) {
            println("Error al marcar tarea como completada: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: Long): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            queries.deleteTask(taskId)
            Result.success(Unit)
        } catch (e: Exception) {
            println("Error al eliminar tarea: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun countTasksByStatus(isCompleted: Boolean): Long = withContext(Dispatchers.Default) {
        try {
            queries.countTasksByStatus(isCompleted).executeAsOne()
        } catch (e: Exception) {
            println("Error al contar tareas: ${e.message}")
            0L
        }
    }

    private fun Tasks.toDomainModel(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            dueDate = dueDate,
            category = category,
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}