package org.agenda.utn

import androidx.compose.runtime.*
import org.agenda.utn.presentation.screens.home.HomeScreen
import org.agenda.utn.presentation.screens.home.HomeViewModel
import org.agenda.utn.presentation.screens.tasklist.TaskListScreen
import org.agenda.utn.presentation.screens.tasklist.TaskListViewModel
import org.agenda.utn.presentation.screens.taskedit.TaskEditScreen
import org.agenda.utn.presentation.screens.taskedit.TaskEditViewModel
import org.agenda.utn.data.local.DatabaseDriverFactory
import org.agenda.utn.database.AgendaDatabase
import org.agenda.utn.data.repository.TaskRepository
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface

sealed class Screen {
    object Home : Screen()
    object TaskList : Screen()
    data class TaskEdit(val taskId: Long?) : Screen()
}

@Composable
expect fun rememberDatabaseDriverFactory(): DatabaseDriverFactory

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    MaterialTheme {
        Surface {
            val driverFactory = rememberDatabaseDriverFactory()
            val driver = remember { driverFactory.createDriver() }
            val database = remember { AgendaDatabase(driver) }
            val repository = remember { TaskRepository(database) }

            when (val screen = currentScreen) {
                is Screen.Home -> {
                    val viewModel = remember { HomeViewModel(repository) }
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToTaskList = { currentScreen = Screen.TaskList },
                        onNavigateToCreateTask = { currentScreen = Screen.TaskEdit(taskId = null) }
                    )
                }

                is Screen.TaskList -> {
                    val viewModel = remember { TaskListViewModel(repository) }
                    TaskListScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = Screen.Home },
                        onNavigateToEdit = { taskId -> currentScreen = Screen.TaskEdit(taskId) }
                    )
                }

                is Screen.TaskEdit -> {
                    val viewModel = remember { TaskEditViewModel(repository) }
                    TaskEditScreen(
                        viewModel = viewModel,
                        taskId = screen.taskId,
                        onNavigateBack = {
                            currentScreen = if (screen.taskId == null) Screen.Home else Screen.TaskList
                        }
                    )
                }
            }
        }
    }
}
