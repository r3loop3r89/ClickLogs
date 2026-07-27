# ClickLogs

A lightweight, offline-only Android habit tracker app. Tap a button to log any configured habit — no internet required.

## Features

- **Dashboard** — Grid of customizable task buttons with emoji, category, today's count, and last-logged time.
- **Add / Edit Tasks** — Name, category chip picker, and 30-emoji icon selector.
- **Reports** — Weekly (7-day) and monthly (30-day) activity summaries with per-task bar charts and daily breakdowns.
- **Offline-first** — All data is stored in a local Room (SQLite) database. No network permission required.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Repository |
| Database | Room 2.6 |
| Navigation | Navigation Compose 2.8 |
| Build | Android Gradle Plugin 8.7, Gradle 8.9 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 (Android 15) |

## Project Structure

```
app/src/main/
├── java/com/clicklogs/
│   ├── ClickLogsApp.kt          # Application class (DI root)
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── db/
│   │   │   ├── entity/Task.kt
│   │   │   ├── entity/LogEntry.kt
│   │   │   ├── dao/TaskDao.kt
│   │   │   ├── dao/LogEntryDao.kt
│   │   │   └── ClickLogsDatabase.kt
│   │   └── repository/
│   │       └── ClickLogsRepository.kt
│   └── ui/
│       ├── theme/               # Material 3 color, type, theme
│       ├── navigation/AppNavGraph.kt
│       ├── dashboard/           # DashboardScreen + ViewModel
│       ├── addtask/             # AddTaskScreen + ViewModel
│       └── reports/             # ReportsScreen + ViewModel
└── res/
    ├── values/strings.xml
    ├── values/themes.xml
    ├── xml/backup_rules.xml
    ├── xml/data_extraction_rules.xml
    └── mipmap-anydpi-v26/ic_launcher.xml
```

## Building

### Prerequisites
- Android Studio Ladybug (2024.2+) or command-line tools
- JDK 17+
- Android SDK with API 35

### Steps

```bash
# Clone the repo
git clone https://github.com/r3loop3r89/ClickLogs.git
cd ClickLogs

# Build debug APK
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Screens

| Screen | Description |
|---|---|
| Dashboard | Main screen with a 2-column grid of habit buttons |
| Add Task | Form to create a new habit (name, category, emoji icon) |
| Edit Task | Same form pre-filled with existing task data |
| Reports | Weekly/monthly activity summary with progress bars |

## Data Model

```
Tasks
  id (UUID PK) · name · category · emoji · createdAt

LogEntries
  id (UUID PK) · taskId (FK → Tasks) · timestamp · notes
```

All data is stored locally via Room. No network permissions are requested.
