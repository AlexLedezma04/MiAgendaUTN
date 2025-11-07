package org.agenda.utn.presentation.screens.tasklist

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.agenda.utn.data.repository.TaskRepository
import org.agenda.utn.domain.model.Task
import org.agenda.utn.domain.model.TaskFilter
import org.agenda.utn.domain.model.TaskSortOrder
import org.agenda.utn.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val categories: List<String> = emptyList(),
    val currentFilter: TaskFilter = TaskFilter.ALL,
    val currentCategory: String? = null,
    val currentSortOrder: TaskSortOrder = TaskSortOrder.DATE_ASC
)

class TaskListViewModel(
    private val repository: TaskRepository
) : BaseViewModel() {

    // Estado de la lista
    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    // Filtros y ordenamiento actuales
    private val _currentFilter = MutableStateFlow(TaskFilter.ALL)
    private val _currentCategory = MutableStateFlow<String?>(null)
    private val _currentSortOrder = MutableStateFlow(TaskSortOrder.DATE_ASC)

    init {
        loadTasks()
        loadCategories()
    }

    private fun loadTasks() {
        viewModelScope.launch(exceptionHandler) {
            combine(
                _currentFilter,
                _currentCategory,
                _currentSortOrder
            ) { filter, category, sortOrder ->
                Triple(filter, category, sortOrder)
            }.collectLatest { (filter, category, sortOrder) ->
                repository.getTasksByFilter(filter, category).collect { tasks ->
                    val sortedTasks = sortTasks(tasks, sortOrder)
                    _uiState.value = _uiState.value.copy(
                        tasks = sortedTasks,
                        currentFilter = filter,
                        currentCategory = category,
                        currentSortOrder = sortOrder
                    )
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch(exceptionHandler) {
            repository.getAllCategoriesFlow().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    private fun sortTasks(tasks: List<Task>, sortOrder: TaskSortOrder): List<Task> {
        return when (sortOrder) {
            TaskSortOrder.DATE_ASC -> tasks.sortedBy { it.dueDate }
            TaskSortOrder.DATE_DESC -> tasks.sortedByDescending { it.dueDate }
            TaskSortOrder.TITLE_ASC -> tasks.sortedBy { it.title.lowercase() }
            TaskSortOrder.TITLE_DESC -> tasks.sortedByDescending { it.title.lowercase() }
            TaskSortOrder.CREATED_ASC -> tasks.sortedBy { it.createdAt }
            TaskSortOrder.CREATED_DESC -> tasks.sortedByDescending { it.createdAt }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _currentFilter.value = filter
    }

    fun setCategory(category: String?) {
        _currentCategory.value = category
    }

    fun setSortOrder(sortOrder: TaskSortOrder) {
        _currentSortOrder.value = sortOrder
    }

    fun toggleTaskCompletion(taskId: Long) {
        launchWithLoading {
            repository.markTaskAsCompleted(taskId)
        }
    }

    fun deleteTask(taskId: Long) {
        launchWithLoading {
            val result = repository.deleteTask(taskId)
            if (result.isFailure) {
                println("Error al eliminar tarea: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun clearFilters() {
        _currentFilter.value = TaskFilter.ALL
        _currentCategory.value = null
    }
}