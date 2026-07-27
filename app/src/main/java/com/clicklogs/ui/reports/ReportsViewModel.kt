package com.clicklogs.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clicklogs.data.db.entity.LogEntry
import com.clicklogs.data.db.entity.Task
import com.clicklogs.data.repository.ClickLogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

enum class ReportPeriod { WEEK, MONTH }

data class TaskStats(
    val task: Task,
    val count: Int,
    val dailyCounts: Map<Long, Int> // day-start-millis -> count
)

data class ReportsUiState(
    val period: ReportPeriod = ReportPeriod.WEEK,
    val taskStats: List<TaskStats> = emptyList(),
    val totalLogs: Int = 0,
    val mostActive: Task? = null,
    val isLoading: Boolean = true
)

class ReportsViewModel(private val repository: ClickLogsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadReport(ReportPeriod.WEEK)
    }

    fun setPeriod(period: ReportPeriod) {
        _uiState.value = _uiState.value.copy(period = period, isLoading = true)
        loadReport(period)
    }

    private fun loadReport(period: ReportPeriod) {
        viewModelScope.launch {
            val (from, to) = getRange(period)
            combine(
                repository.allTasks,
                repository.getLogEntriesInRange(from, to)
            ) { tasks, entries ->
                buildStats(tasks, entries)
            }.collect { stats ->
                val total = stats.sumOf { it.count }
                val mostActive = stats.maxByOrNull { it.count }?.task
                _uiState.value = _uiState.value.copy(
                    taskStats = stats.sortedByDescending { it.count },
                    totalLogs = total,
                    mostActive = mostActive,
                    isLoading = false
                )
            }
        }
    }

    private fun buildStats(tasks: List<Task>, entries: List<LogEntry>): List<TaskStats> {
        val entriesByTask = entries.groupBy { it.taskId }
        return tasks.map { task ->
            val taskEntries = entriesByTask[task.id] ?: emptyList()
            val dailyCounts = taskEntries.groupBy { dayStart(it.timestamp) }
                .mapValues { (_, v) -> v.size }
            TaskStats(task, taskEntries.size, dailyCounts)
        }
    }

    private fun dayStart(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getRange(period: ReportPeriod): Pair<Long, Long> {
        val to = System.currentTimeMillis()
        val days = if (period == ReportPeriod.WEEK) 7L else 30L
        val from = to - TimeUnit.DAYS.toMillis(days)
        return from to to
    }

    class Factory(private val repository: ClickLogsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReportsViewModel(repository) as T
        }
    }
}
