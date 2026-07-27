package com.clicklogs.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clicklogs.data.db.entity.Task
import com.clicklogs.data.repository.ClickLogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class TaskWithLastLog(
    val task: Task,
    val lastLoggedAt: Long? = null,
    val todayCount: Int = 0
)

data class DashboardUiState(
    val tasks: List<TaskWithLastLog> = emptyList(),
    val isLoading: Boolean = true,
    val snackbarMessage: String? = null
)

class DashboardViewModel(private val repository: ClickLogsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        val startOfDay = getStartOfToday()
        val endOfDay = startOfDay + TimeUnit.DAYS.toMillis(1)
        viewModelScope.launch {
            combine(
                repository.allTasks,
                repository.getLogEntriesInRange(startOfDay, endOfDay)
            ) { tasks, todayLogs ->
                val countByTask = todayLogs.groupingBy { it.taskId }.eachCount()
                tasks to countByTask
            }.collect { (tasks, countByTask) ->
                val tasksWithLogs = tasks.map { task ->
                    val lastLogged = repository.getLastLogTimestamp(task.id)
                    TaskWithLastLog(
                        task = task,
                        lastLoggedAt = lastLogged,
                        todayCount = countByTask[task.id] ?: 0
                    )
                }
                _uiState.value = DashboardUiState(
                    tasks = tasksWithLogs,
                    isLoading = false,
                    snackbarMessage = _uiState.value.snackbarMessage
                )
            }
        }
    }

    fun logTask(taskId: String, taskName: String) {
        viewModelScope.launch {
            repository.logTask(taskId)
            _uiState.value = _uiState.value.copy(snackbarMessage = "Logged: $taskName")
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    private fun getStartOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    class Factory(private val repository: ClickLogsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(repository) as T
        }
    }
}
