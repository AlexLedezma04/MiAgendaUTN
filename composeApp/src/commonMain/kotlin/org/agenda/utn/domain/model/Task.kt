package org.agenda.utn.domain.model

data class Task(
    val id: Long = 0L,
    val title: String,
    val description: String,
    val dueDate: Long,
    val category: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        val DEFAULT_CATEGORIES = listOf(
            "Trabajo",
            "Personal",
            "Estudio",
            "Hogar",
            "Salud",
            "Compras",
            "Otros"
        )
    }
}

enum class TaskFilter {
    ALL,        // Todas las tareas
    PENDING,    // Solo pendientes
    COMPLETED   // Solo completadas
}

enum class TaskSortOrder {
    DATE_ASC,       // Fecha ascendente (más cercana primero)
    DATE_DESC,      // Fecha descendente (más lejana primero)
    TITLE_ASC,      // Título A-Z
    TITLE_DESC,     // Título Z-A
    CREATED_ASC,    // Creadas más antiguas primero
    CREATED_DESC    // Creadas más recientes primero
}