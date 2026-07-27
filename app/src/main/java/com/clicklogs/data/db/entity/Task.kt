package com.clicklogs.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String = "",
    val emoji: String = "📌",
    val createdAt: Long = System.currentTimeMillis()
)
