package org.agenda.utn.presentation.screens.tasklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.agenda.utn.domain.model.Task
import org.agenda.utn.domain.model.TaskFilter
import org.agenda.utn.domain.model.TaskSortOrder
import org.agenda.utn.util.DateUtils

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Botón de filtros
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, "Filtrar")
                    }
                    // Botón de ordenamiento
                    IconButton(onClick = { showSortDialog = true }) {
                        Icon(Icons.Default.Sort, "Ordenar")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToEdit(null) },
                backgroundColor = MaterialTheme.colors.secondary
            ) {
                Icon(Icons.Default.Add, "Nueva tarea")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Chips de filtros activos
            FilterChips(
                currentFilter = uiState.currentFilter,
                currentCategory = uiState.currentCategory,
                onClearFilters = { viewModel.clearFilters() }
            )

            // Lista de tareas
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.tasks.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.tasks,
                        key = { it.id }
                    ) { task ->
                        TaskItem(
                            task = task,
                            onTaskClick = { onNavigateToEdit(task.id) },
                            onCompleteClick = { viewModel.toggleTaskCompletion(task.id) },
                            onDeleteClick = { taskToDelete = task.id }
                        )
                    }
                }
            }
        }

        // Diálogo de filtros
        if (showFilterDialog) {
            FilterDialog(
                currentFilter = uiState.currentFilter,
                currentCategory = uiState.currentCategory,
                categories = uiState.categories,
                onDismiss = { showFilterDialog = false },
                onFilterSelected = { filter, category ->
                    viewModel.setFilter(filter)
                    viewModel.setCategory(category)
                    showFilterDialog = false
                }
            )
        }

        // Diálogo de ordenamiento
        if (showSortDialog) {
            SortDialog(
                currentSortOrder = uiState.currentSortOrder,
                onDismiss = { showSortDialog = false },
                onSortSelected = { sortOrder ->
                    viewModel.setSortOrder(sortOrder)
                    showSortDialog = false
                }
            )
        }

        // Diálogo de confirmación de eliminación
        taskToDelete?.let { taskId ->
            AlertDialog(
                onDismissRequest = { taskToDelete = null },
                title = { Text("Eliminar tarea") },
                text = { Text("¿Estás seguro de que deseas eliminar esta tarea?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteTask(taskId)
                            taskToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { taskToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun FilterChips(
    currentFilter: TaskFilter,
    currentCategory: String?,
    onClearFilters: () -> Unit
) {
    if (currentFilter != TaskFilter.ALL || currentCategory != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filtros:", style = MaterialTheme.typography.caption)

            if (currentFilter != TaskFilter.ALL) {
                Chip(
                    text = when (currentFilter) {
                        TaskFilter.PENDING -> "Pendientes"
                        TaskFilter.COMPLETED -> "Completadas"
                        else -> ""
                    },
                    onClose = onClearFilters
                )
            }

            currentCategory?.let {
                Chip(text = it, onClose = onClearFilters)
            }
        }
    }
}

@Composable
private fun Chip(text: String, onClose: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colors.primary.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, style = MaterialTheme.typography.caption)
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Default.Close,
                contentDescription = "Eliminar filtro",
                modifier = Modifier.size(16.dp).clickable(onClick = onClose)
            )
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTaskClick),
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onCompleteClick() }
            )

            Spacer(Modifier.width(12.dp))

            // Contenido de la tarea
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )

                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Fecha
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (DateUtils.isPastDate(task.dueDate) && !task.isCompleted) {
                                Color.Red
                            } else Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = DateUtils.formatDate(task.dueDate),
                            style = MaterialTheme.typography.caption,
                            color = if (DateUtils.isPastDate(task.dueDate) && !task.isCompleted) {
                                Color.Red
                            } else Color.Gray
                        )
                    }

                    Text("•", color = Color.Gray)

                    // Categoría
                    Text(
                        text = task.category,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.primary
                    )
                }
            }

            // Botón de eliminar
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Assignment,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "No hay tareas",
                style = MaterialTheme.typography.h6,
                color = Color.Gray
            )
            Text(
                text = "Crea una nueva tarea para comenzar",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun FilterDialog(
    currentFilter: TaskFilter,
    currentCategory: String?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onFilterSelected: (TaskFilter, String?) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(currentFilter) }
    var selectedCategory by remember { mutableStateOf(currentCategory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar tareas") },
        text = {
            Column {
                Text("Estado:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                // Filtros de estado
                TaskFilter.values().forEach { filter ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFilter = filter }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            when (filter) {
                                TaskFilter.ALL -> "Todas"
                                TaskFilter.PENDING -> "Pendientes"
                                TaskFilter.COMPLETED -> "Completadas"
                            }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text("Categoría:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                // Opción "Todas las categorías"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCategory = null }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Todas las categorías")
                }

                // Categorías disponibles
                categories.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = category }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(category)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onFilterSelected(selectedFilter, selectedCategory) }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun SortDialog(
    currentSortOrder: TaskSortOrder,
    onDismiss: () -> Unit,
    onSortSelected: (TaskSortOrder) -> Unit
) {
    var selectedSort by remember { mutableStateOf(currentSortOrder) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ordenar tareas") },
        text = {
            Column {
                TaskSortOrder.values().forEach { sortOrder ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSort = sortOrder }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSort == sortOrder,
                            onClick = { selectedSort = sortOrder }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            when (sortOrder) {
                                TaskSortOrder.DATE_ASC -> "Fecha (más cercana primero)"
                                TaskSortOrder.DATE_DESC -> "Fecha (más lejana primero)"
                                TaskSortOrder.TITLE_ASC -> "Título (A-Z)"
                                TaskSortOrder.TITLE_DESC -> "Título (Z-A)"
                                TaskSortOrder.CREATED_ASC -> "Más antiguas primero"
                                TaskSortOrder.CREATED_DESC -> "Más recientes primero"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSortSelected(selectedSort) }) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}