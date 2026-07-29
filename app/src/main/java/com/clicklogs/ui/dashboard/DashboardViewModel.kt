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
    val todayCount: Int = 0,
    val last7DaysLogged: List<Boolean> = emptyList()
)

data class DashboardUiState(
    val tasks: List<TaskWithLastLog> = emptyList(),
    val isLoading: Boolean = true,
    val snackbarMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val availableCategories: List<String> = emptyList()
)

class DashboardViewModel(private val repository: ClickLogsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")

    init {
        observeTasks()
    }

    private fun observeTasks() {
        val startOfMonday = getStartOfCurrentWeekMonday()
        val endOfSunday = startOfMonday + TimeUnit.DAYS.toMillis(7)

        viewModelScope.launch {
            combine(
                repository.allTasks,
                repository.getLogEntriesInRange(startOfMonday, endOfSunday),
                _searchQuery,
                _selectedCategory
            ) { tasks, logsInRange, query, category ->
                val logsByTask = logsInRange.groupBy { it.taskId }
                val startOfDay = getStartOfToday()

                val allTasksWithLogs = tasks.map { task ->
                    val taskLogs = logsByTask[task.id] ?: emptyList()
                    val todayCount = taskLogs.count { it.timestamp >= startOfDay }
                    val last7Days = (0..6).map { dayIndex ->
                        val dayStart = startOfMonday + TimeUnit.DAYS.toMillis(dayIndex.toLong())
                        val dayEnd = dayStart + TimeUnit.DAYS.toMillis(1)
                        taskLogs.any { it.timestamp in dayStart until dayEnd }
                    }

                    // Get last log from the in-memory week logs if possible, otherwise read from DB
                    val lastLoggedInRange = taskLogs.maxOfOrNull { it.timestamp }
                    val lastLogged = lastLoggedInRange ?: repository.getLastLogTimestamp(task.id)

                    TaskWithLastLog(
                        task = task,
                        lastLoggedAt = lastLogged,
                        todayCount = todayCount,
                        last7DaysLogged = last7Days
                    )
                }

                // Filter task list by category and search query
                val filteredTasks = allTasksWithLogs.filter { taskWithLog ->
                    val matchesCategory = category == "All" || 
                            taskWithLog.task.category.equals(category, ignoreCase = true)
                    val matchesQuery = query.isBlank() || 
                            taskWithLog.task.name.contains(query, ignoreCase = true) ||
                            taskWithLog.task.category.contains(query, ignoreCase = true)
                    
                    matchesCategory && matchesQuery
                }

                // Extract all unique categories
                val categories = tasks.map { it.category }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()

                Triple(filteredTasks, categories, Pair(query, category))
            }.collect { (filteredTasks, categories, queryAndCategory) ->
                val (query, category) = queryAndCategory
                _uiState.value = DashboardUiState(
                    tasks = filteredTasks,
                    isLoading = false,
                    snackbarMessage = _uiState.value.snackbarMessage,
                    searchQuery = query,
                    selectedCategory = category,
                    availableCategories = listOf("All") + categories
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

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
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

    private fun getStartOfCurrentWeekMonday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val daysToSubtract = when (dayOfWeek) {
            Calendar.SUNDAY -> 6
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            else -> 0
        }
        cal.add(Calendar.DAY_OF_YEAR, -daysToSubtract)
        return cal.timeInMillis
    }

    class Factory(private val repository: ClickLogsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(repository) as T
        }
    }
}
