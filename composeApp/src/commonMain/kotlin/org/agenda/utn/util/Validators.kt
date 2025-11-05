package org.agenda.utn.util

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

object Validators {

    fun validateTitle(title: String): ValidationResult {
        return when {
            title.isBlank() -> ValidationResult.Error("El título no puede estar vacío")
            title.length < 3 -> ValidationResult.Error("El título debe tener al menos 3 caracteres")
            title.length > 100 -> ValidationResult.Error("El título no puede exceder 100 caracteres")
            else -> ValidationResult.Success
        }
    }

    fun validateDescription(description: String): ValidationResult {
        return when {
            description.length > 500 -> ValidationResult.Error("La descripción no puede exceder 500 caracteres")
            else -> ValidationResult.Success
        }
    }

    fun validateCategory(category: String): ValidationResult {
        return when {
            category.isBlank() -> ValidationResult.Error("Debe seleccionar una categoría")
            else -> ValidationResult.Success
        }
    }

    fun validateTask(title: String, description: String, category: String): ValidationResult {
        validateTitle(title).let { if (it is ValidationResult.Error) return it }
        validateDescription(description).let { if (it is ValidationResult.Error) return it }
        validateCategory(category).let { if (it is ValidationResult.Error) return it }

        return ValidationResult.Success
    }
}