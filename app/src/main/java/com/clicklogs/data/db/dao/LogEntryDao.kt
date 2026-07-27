package com.clicklogs.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clicklogs.data.db.entity.LogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface LogEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogEntry(logEntry: LogEntry)

    @Query("SELECT * FROM log_entries WHERE taskId = :taskId ORDER BY timestamp DESC")
    fun getLogEntriesForTask(taskId: String): Flow<List<LogEntry>>

    @Query("SELECT * FROM log_entries WHERE timestamp >= :from AND timestamp <= :to ORDER BY timestamp DESC")
    fun getLogEntriesInRange(from: Long, to: Long): Flow<List<LogEntry>>

    @Query("SELECT * FROM log_entries ORDER BY timestamp DESC")
    fun getAllLogEntries(): Flow<List<LogEntry>>

    @Query("SELECT COUNT(*) FROM log_entries WHERE taskId = :taskId AND timestamp >= :from AND timestamp <= :to")
    suspend fun countLogsForTask(taskId: String, from: Long, to: Long): Int

    @Query("SELECT MAX(timestamp) FROM log_entries WHERE taskId = :taskId")
    suspend fun getLastLogTimestamp(taskId: String): Long?

    @Query("DELETE FROM log_entries WHERE id = :id")
    suspend fun deleteLogEntry(id: String)

    @Query("DELETE FROM log_entries WHERE taskId = :taskId")
    suspend fun deleteAllLogsForTask(taskId: String)
}
