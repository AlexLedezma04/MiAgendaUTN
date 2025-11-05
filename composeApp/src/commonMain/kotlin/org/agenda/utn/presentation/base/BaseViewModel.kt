package org.agenda.utn.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    protected fun launchWithLoading(block: suspend () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            try {
                _isLoading.value = true
                block()
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleError(exception: Throwable) {
        val message = exception.message ?: "Error desconocido"
        _errorMessage.value = message
        println("ERROR: $message")
        exception.printStackTrace()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}