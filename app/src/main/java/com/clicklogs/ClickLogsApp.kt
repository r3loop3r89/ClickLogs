package com.clicklogs

import android.app.Application
import com.clicklogs.data.db.ClickLogsDatabase
import com.clicklogs.data.repository.ClickLogsRepository

class ClickLogsApp : Application() {

    val database by lazy { ClickLogsDatabase.getDatabase(this) }

    val repository by lazy {
        ClickLogsRepository(
            taskDao = database.taskDao(),
            logEntryDao = database.logEntryDao()
        )
    }
}
