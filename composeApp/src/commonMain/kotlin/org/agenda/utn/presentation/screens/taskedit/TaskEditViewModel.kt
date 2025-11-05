package org.agenda.utn.presentation.screens.taskedit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.agenda.utn.data.repository.TaskRepository
import org.agenda.utn.domain.model.Task
import org.agenda.utn.presentation.base.BaseViewModel
import org.agenda.utn.util.DateUtils
import org.agenda.utn.util.ValidationResult
import org.agenda.utn.util.Validators

data class TaskEditUiState(
    val taskId: Long? = null,
    val title: String = "",
    val description: String = "",
    val dueDate: Long = DateUtils.getTodayStartTimestamp(),
    val category: String = "",
    val isCompleted: Boolean = false,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val categoryError: String? = null,
    val isEditMode: Boolean = false,
    val isSaved: Boolean = false
)

class TaskEditViewModel(
    private val repository: TaskRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(TaskEditUiState())
    val uiState: StateFlow<TaskEditUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: Long) {
        launchWithLoading {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                _uiState.value = TaskEditUiState(
                    taskId = task.id,
                    title = task.title,
                    description = task.description,
                    dueDate = task.dueDate,
                    category = task.category,
                    isCompleted = task.isCompleted,
                    isEditMode = true
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            titleError = null
        )
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(
            description = description,
            descriptionError = null
        )
    }

    fun updateDueDate(timestamp: Long) {
        _uiState.value = _uiState.value.copy(dueDate = timestamp)
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(
            category = category,
            categoryError = null
        )
    }

    fun saveTask() {
        val state = _uiState.value

        // Validar título
        val titleValidation = Validators.validateTitle(state.title)
        if (titleValidation is ValidationResult.Error) {
            _uiState.value = state.copy(titleError = titleValidation.message)
            return
        }

        // Validar descripción
        val descValidation = Validators.validateDescription(state.description)
        if (descValidation is ValidationResult.Error) {
            _uiState.value = state.copy(descriptionError = descValidation.message)
            return
        }

        // Validar categoría
        val categoryValidation = Validators.validateCategory(state.category)
        if (categoryValidation is ValidationResult.Error) {
            _uiState.value = state.copy(categoryError = categoryValidation.message)
            return
        }

        // Si todas las validaciones pasan, guardar
        launchWithLoading {
            val task = Task(
                id = state.taskId ?: 0L,
                title = state.title.trim(),
                description = state.description.trim(),
                dueDate = state.dueDate,
                category = state.category,
                isCompleted = state.isCompleted
            )

            val result = if (state.isEditMode) {
                repository.updateTask(task)
            } else {
                repository.insertTask(task)
            }

            if (result.isSuccess) {
                _uiState.value = state.copy(isSaved = true)
            }
        }
    }

    fun resetSavedState() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}