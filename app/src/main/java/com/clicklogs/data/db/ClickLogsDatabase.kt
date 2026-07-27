package com.clicklogs.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.clicklogs.data.db.dao.LogEntryDao
import com.clicklogs.data.db.dao.TaskDao
import com.clicklogs.data.db.entity.LogEntry
import com.clicklogs.data.db.entity.Task

@Database(
    entities = [Task::class, LogEntry::class],
    version = 1,
    exportSchema = false
)
abstract class ClickLogsDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun logEntryDao(): LogEntryDao

    companion object {
        @Volatile
        private var INSTANCE: ClickLogsDatabase? = null

        fun getDatabase(context: Context): ClickLogsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClickLogsDatabase::class.java,
                    "clicklogs_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
