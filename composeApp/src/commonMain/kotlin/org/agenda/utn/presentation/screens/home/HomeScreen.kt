package org.agenda.utn.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.agenda.utn.domain.model.Task
import org.agenda.utn.util.DateUtils

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToTaskList: () -> Unit,
    onNavigateToCreateTask: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agenda de Tareas") },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTask,
                backgroundColor = MaterialTheme.colors.secondary
            ) {
                Icon(Icons.Default.Add, "Nueva tarea")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título de bienvenida
                item {
                    Text(
                        text = "Resumen",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Tarjetas de estadísticas
                item {
                    StatisticsCards(uiState)
                }

                // Botón para ver todas las tareas
                item {
                    Button(
                        onClick = onNavigateToTaskList,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary
                        )
                    ) {
                        Icon(Icons.Default.List, "Ver tareas", modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Ver todas las tareas")
                    }
                }

                // Tareas recientes
                if (uiState.recentTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "Tareas Recientes",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(uiState.recentTasks) { task ->
                        RecentTaskItem(
                            task = task,
                            onTaskClick = onNavigateToTaskList,
                            onCompleteClick = { viewModel.markTaskAsCompleted(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticsCards(uiState: HomeUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total",
                value = uiState.totalTasks.toString(),
                icon = Icons.Default.List,
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Completadas",
                value = uiState.completedTasks.toString(),
                icon = Icons.Default.CheckCircle,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Pendientes",
                value = uiState.pendingTasks.toString(),
                icon = Icons.Default.Schedule,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Vencidas",
                value = uiState.overdueTasks.toString(),
                icon = Icons.Default.Warning,
                color = Color(0xFFF44336),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .background(color.copy(alpha = 0.1f))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun RecentTaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTaskClick),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox de completado
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onCompleteClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.primary
                )
            )

            Spacer(Modifier.width(12.dp))

            // Información de la tarea
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = DateUtils.getRelativeDateDescription(task.dueDate),
                        style = MaterialTheme.typography.caption,
                        color = if (DateUtils.isPastDate(task.dueDate) && !task.isCompleted) {
                            Color.Red
                        } else {
                            Color.Gray
                        }
                    )
                    Text("•", color = Color.Gray)
                    Text(
                        text = task.category,
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}