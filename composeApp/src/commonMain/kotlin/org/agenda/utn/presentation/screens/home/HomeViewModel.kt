package org.agenda.utn.presentation.screens.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.agenda.utn.data.repository.TaskRepository
import org.agenda.utn.presentation.base.BaseViewModel

class HomeViewModel(
    private val repository: TaskRepository
) : BaseViewModel() {

    // Estado de la pantalla de inicio
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadSummary()
    }

    private fun loadSummary() {
        viewModelScope.launch(exceptionHandler) {
            // Combinar múltiples flows para obtener estadísticas
            combine(
                repository.getAllTasksFlow(),
                repository.getAllCategoriesFlow()
            ) { tasks, categories ->
                val totalTasks = tasks.size
                val completedTasks = tasks.count { it.isCompleted }
                val pendingTasks = totalTasks - completedTasks
                val todayTasks = tasks.filter { isToday(it.dueDate) }
                val overdueTasks = tasks.filter { !it.isCompleted && isPastDue(it.dueDate) }

                HomeUiState(
                    totalTasks = totalTasks,
                    completedTasks = completedTasks,
                    pendingTasks = pendingTasks,
                    todayTasks = todayTasks.size,
                    overdueTasks = overdueTasks.size,
                    categories = categories,
                    recentTasks = tasks.sortedByDescending { it.createdAt }.take(5)
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun markTaskAsCompleted(taskId: Long) {
        launchWithLoading {
            repository.markTaskAsCompleted(taskId)
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        return org.agenda.utn.util.DateUtils.isToday(timestamp)
    }

    private fun isPastDue(timestamp: Long): Boolean {
        return org.agenda.utn.util.DateUtils.isPastDate(timestamp)
    }
}

data class HomeUiState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val todayTasks: Int = 0,
    val overdueTasks: Int = 0,
    val categories: List<String> = emptyList(),
    val recentTasks: List<org.agenda.utn.domain.model.Task> = emptyList()
)