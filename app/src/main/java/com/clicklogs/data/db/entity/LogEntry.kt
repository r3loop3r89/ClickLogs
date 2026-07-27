package com.clicklogs.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "log_entries",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId")]
)
data class LogEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
