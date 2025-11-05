package org.agenda.utn.presentation.screens.taskedit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import org.agenda.utn.domain.model.Task
import org.agenda.utn.util.DateUtils

@Composable
fun TaskEditScreen(
    viewModel: TaskEditViewModel,
    taskId: Long?,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Cargar tarea si es modo edición
    LaunchedEffect(taskId) {
        taskId?.let {
            viewModel.loadTask(it)
        }
    }

    // Navegar de vuelta cuando se guarda exitosamente
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Editar Tarea" else "Nueva Tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveTask() },
                backgroundColor = MaterialTheme.colors.secondary
            ) {
                Icon(Icons.Default.Save, "Guardar")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo de título
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Título *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.titleError != null,
                    singleLine = true
                )
                if (uiState.titleError != null) {
                    Text(
                        text = uiState.titleError!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.caption
                    )
                }

                // Campo de descripción
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    isError = uiState.descriptionError != null,
                    maxLines = 5
                )
                if (uiState.descriptionError != null) {
                    Text(
                        text = uiState.descriptionError!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.caption
                    )
                }

                // Selector de fecha
                OutlinedTextField(
                    value = DateUtils.formatDate(uiState.dueDate),
                    onValueChange = {},
                    label = { Text("Fecha de vencimiento *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colors.onSurface,
                        disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                        disabledLabelColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                    ),
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, "Seleccionar fecha")
                    }
                )

                // Selector de categoría
                OutlinedTextField(
                    value = uiState.category,
                    onValueChange = {},
                    label = { Text("Categoría *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCategoryDialog = true },
                    enabled = false,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colors.onSurface,
                        disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                        disabledLabelColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                    ),
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, "Seleccionar categoría")
                    },
                    isError = uiState.categoryError != null
                )
                if (uiState.categoryError != null) {
                    Text(
                        text = uiState.categoryError!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.caption
                    )
                }

                // Checkbox de tarea completada (solo en modo edición)
                if (uiState.isEditMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.isCompleted,
                            onCheckedChange = { /* La tarea se marca completada desde la lista */ }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Tarea completada")
                    }
                }

                Text(
                    text = "* Campos obligatorios",
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
            }

            // Indicador de carga
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Diálogo de selección de categoría
        if (showCategoryDialog) {
            CategoryDialog(
                selectedCategory = uiState.category,
                onDismiss = { showCategoryDialog = false },
                onCategorySelected = { category ->
                    viewModel.updateCategory(category)
                    showCategoryDialog = false
                }
            )
        }

        // Diálogo de selección de fecha
        if (showDatePicker) {
            DatePickerDialog(
                currentDate = uiState.dueDate,
                onDismiss = { showDatePicker = false },
                onDateSelected = { timestamp ->
                    viewModel.updateDueDate(timestamp)
                    showDatePicker = false
                }
            )
        }

        // Mostrar errores
        errorMessage?.let { error ->
            LaunchedEffect(error) {
                // Aquí se podría mostrar un Snackbar
                println("Error: $error")
            }
        }
    }
}

@Composable
private fun CategoryDialog(
    selectedCategory: String,
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar categoría") },
        text = {
            Column {
                Task.DEFAULT_CATEGORIES.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCategorySelected(category) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == category,
                            onClick = { onCategorySelected(category) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(category)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DatePickerDialog(
    currentDate: Long,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val instant = Instant.fromEpochMilliseconds(currentDate)
    val currentLocalDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

    var selectedYear by remember { mutableStateOf(currentLocalDate.year) }
    var selectedMonth by remember { mutableStateOf(currentLocalDate.monthNumber) }
    var selectedDay by remember { mutableStateOf(currentLocalDate.dayOfMonth) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar fecha") },
        text = {
            Column {
                // Selector de año
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Año:")
                    Row {
                        TextButton(onClick = { selectedYear-- }) { Text("-") }
                        Text("$selectedYear", modifier = Modifier.padding(horizontal = 8.dp))
                        TextButton(onClick = { selectedYear++ }) { Text("+") }
                    }
                }

                // Selector de mes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mes:")
                    Row {
                        TextButton(onClick = {
                            selectedMonth = if (selectedMonth > 1) selectedMonth - 1 else 12
                        }) { Text("-") }
                        Text(
                            getMonthName(selectedMonth),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        TextButton(onClick = {
                            selectedMonth = if (selectedMonth < 12) selectedMonth + 1 else 1
                        }) { Text("+") }
                    }
                }

                // Selector de día
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Día:")
                    Row {
                        TextButton(onClick = {
                            if (selectedDay > 1) selectedDay--
                        }) { Text("-") }
                        Text("$selectedDay", modifier = Modifier.padding(horizontal = 8.dp))
                        TextButton(onClick = {
                            val maxDays = getDaysInMonth(selectedYear, selectedMonth)
                            if (selectedDay < maxDays) selectedDay++
                        }) { Text("+") }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val timestamp = DateUtils.getTimestampFromDate(selectedYear, selectedMonth, selectedDay)
                onDateSelected(timestamp)
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Enero"
        2 -> "Febrero"
        3 -> "Marzo"
        4 -> "Abril"
        5 -> "Mayo"
        6 -> "Junio"
        7 -> "Julio"
        8 -> "Agosto"
        9 -> "Septiembre"
        10 -> "Octubre"
        11 -> "Noviembre"
        12 -> "Diciembre"
        else -> "Mes inválido"
    }
}

private fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 31
    }
}