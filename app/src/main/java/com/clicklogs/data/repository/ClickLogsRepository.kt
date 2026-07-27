package com.clicklogs.data.repository

import com.clicklogs.data.db.dao.LogEntryDao
import com.clicklogs.data.db.dao.TaskDao
import com.clicklogs.data.db.entity.LogEntry
import com.clicklogs.data.db.entity.Task
import kotlinx.coroutines.flow.Flow

class ClickLogsRepository(
    private val taskDao: TaskDao,
    private val logEntryDao: LogEntryDao
) {

    // Task operations
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: String) = taskDao.deleteTaskById(id)

    suspend fun getTaskById(id: String): Task? = taskDao.getTaskById(id)

    // Log entry operations
    val allLogEntries: Flow<List<LogEntry>> = logEntryDao.getAllLogEntries()

    suspend fun logTask(taskId: String) {
        logEntryDao.insertLogEntry(LogEntry(taskId = taskId))
    }

    fun getLogEntriesForTask(taskId: String): Flow<List<LogEntry>> =
        logEntryDao.getLogEntriesForTask(taskId)

    fun getLogEntriesInRange(from: Long, to: Long): Flow<List<LogEntry>> =
        logEntryDao.getLogEntriesInRange(from, to)

    suspend fun countLogsForTask(taskId: String, from: Long, to: Long): Int =
        logEntryDao.countLogsForTask(taskId, from, to)

    suspend fun getLastLogTimestamp(taskId: String): Long? =
        logEntryDao.getLastLogTimestamp(taskId)

    suspend fun deleteLogEntry(id: String) = logEntryDao.deleteLogEntry(id)
}
