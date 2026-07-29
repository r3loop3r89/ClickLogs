package com.clicklogs.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.foundation.gestures.detectTapGestures
import android.content.Context
import android.os.Vibrator
import android.os.VibrationEffect
import android.os.VibratorManager
import android.os.Build
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clicklogs.data.db.entity.Task
import com.clicklogs.data.repository.ClickLogsRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class TaskCategoryTheme(
    val primaryColor: Color,
    val containerColor: Color,
    val onContainerColor: Color,
    val gradientColors: List<Color>
)

private fun getCategoryTheme(category: String, isDark: Boolean): TaskCategoryTheme {
    val cat = category.trim().lowercase()
    return when {
        cat.contains("health") || cat.contains("wellness") || cat.contains("medicine") || cat.contains("pill") || cat.contains("care") -> {
            if (isDark) {
                TaskCategoryTheme(
                    primaryColor = Color(0xFF81C784),
                    containerColor = Color(0xFF1B3820),
                    onContainerColor = Color(0xFFC8E6C9),
                    gradientColors = listOf(Color(0xFF132A17), Color(0xFF1E3A23))
                )
            } else {
                TaskCategoryTheme(
                    primaryColor = Color(0xFF2E7D32),
                    containerColor = Color(0xFFE8F5E9),
                    onContainerColor = Color(0xFF1B5E20),
                    gradientColors = listOf(Color(0xFFF1FDF3), Color(0xFFEAF7EB))
                )
            }
        }
        cat.contains("fit") || cat.contains("sport") || cat.contains("run") || cat.contains("walk") || cat.contains("gym") || cat.contains("exer") || cat.contains("train") -> {
            if (isDark) {
                TaskCategoryTheme(
                    primaryColor = Color(0xFFFF8A65),
                    containerColor = Color(0xFF3E1F15),
                    onContainerColor = Color(0xFFFFCCBC),
                    gradientColors = listOf(Color(0xFF2A150F), Color(0xFF3C2017))
                )
            } else {
                TaskCategoryTheme(
                    primaryColor = Color(0xFFD84315),
                    containerColor = Color(0xFFFBE9E7),
                    onContainerColor = Color(0xFFBF360C),
                    gradientColors = listOf(Color(0xFFFFF3F0), Color(0xFFFCECE8))
                )
            }
        }
        cat.contains("food") || cat.contains("drink") || cat.contains("water") || cat.contains("beer") || cat.contains("eat") || cat.contains("coffee") || cat.contains("tea") -> {
            if (isDark) {
                TaskCategoryTheme(
                    primaryColor = Color(0xFFFFD54F),
                    containerColor = Color(0xFF3B2F0F),
                    onContainerColor = Color(0xFFFFE082),
                    gradientColors = listOf(Color(0xFF271F0A), Color(0xFF3D3010))
                )
            } else {
                TaskCategoryTheme(
                    primaryColor = Color(0xFFEF6C00),
                    containerColor = Color(0xFFFFF3E0),
                    onContainerColor = Color(0xFFE65100),
                    gradientColors = listOf(Color(0xFFFFFBF3), Color(0xFFFFF3E5))
                )
            }
        }
        cat.contains("work") || cat.contains("study") || cat.contains("book") || cat.contains("prod") || cat.contains("learn") || cat.contains("job") || cat.contains("office") -> {
            if (isDark) {
                TaskCategoryTheme(
                    primaryColor = Color(0xFF64B5F6),
                    containerColor = Color(0xFF15293C),
                    onContainerColor = Color(0xFFBBDEFB),
                    gradientColors = listOf(Color(0xFF0D1D2C), Color(0xFF162D42))
                )
            } else {
                TaskCategoryTheme(
                    primaryColor = Color(0xFF1565C0),
                    containerColor = Color(0xFFE3F2FD),
                    onContainerColor = Color(0xFF0D47A1),
                    gradientColors = listOf(Color(0xFFF2F8FD), Color(0xFFE4F0FB))
                )
            }
        }
        cat.contains("pers") || cat.contains("hobby") || cat.contains("leis") || cat.contains("self") || cat.contains("mind") || cat.contains("yoga") || cat.contains("medit") -> {
            if (isDark) {
                TaskCategoryTheme(
                    primaryColor = Color(0xFFBA68C8),
                    containerColor = Color(0xFF32193C),
                    onContainerColor = Color(0xFFE1BEE7),
                    gradientColors = listOf(Color(0xFF23102C), Color(0xFF351C40))
                )
            } else {
                TaskCategoryTheme(
                    primaryColor = Color(0xFF7B1FA2),
                    containerColor = Color(0xFFF3E5F5),
                    onContainerColor = Color(0xFF4A148C),
                    gradientColors = listOf(Color(0xFFFAF2FC), Color(0xFFF3E7F7))
                )
            }
        }
        else -> {
            if (isDark) {
                TaskCategoryTheme(
                    primaryColor = Color(0xFF80DEEA),
                    containerColor = Color(0xFF112E35),
                    onContainerColor = Color(0xFFE0F7FA),
                    gradientColors = listOf(Color(0xFF0B1F24), Color(0xFF13323B))
                )
            } else {
                TaskCategoryTheme(
                    primaryColor = Color(0xFF00796B),
                    containerColor = Color(0xFFE0F2F1),
                    onContainerColor = Color(0xFF004D40),
                    gradientColors = listOf(Color(0xFFF3FBFB), Color(0xFFE2F3F2))
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    repository: ClickLogsRepository,
    onNavigateToAddTask: () -> Unit,
    onNavigateToReports: () -> Unit,
    onEditTask: (String) -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory(repository))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var isSearchActive by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val hasNoTasksAtAll = uiState.tasks.isEmpty() && uiState.searchQuery.isEmpty() && uiState.selectedCategory == "All"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            placeholder = {
                                Text(
                                    text = "Search habits...",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge,
                            trailingIcon = {
                                IconButton(onClick = {
                                    viewModel.setSearchQuery("")
                                    isSearchActive = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close search",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        )
                    } else {
                        Text(
                            text = "ClickLogs",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToReports) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Reports"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...")
                }
            }
            hasNoTasksAtAll -> {
                EmptyDashboard(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    onAddTask = onNavigateToAddTask
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Dashboard Header (Spans all columns)
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        val totalTodayLogs = uiState.tasks.sumOf { it.todayCount }
                        val activeHabitsCount = uiState.tasks.count { it.todayCount > 0 }
                        val mostActive = uiState.tasks
                            .filter { it.todayCount > 0 }
                            .maxByOrNull { it.todayCount }

                        DashboardHeader(
                            totalTodayLogs = totalTodayLogs,
                            activeHabitsCount = activeHabitsCount,
                            mostActiveHabitName = mostActive?.task?.name,
                            mostActiveHabitEmoji = mostActive?.task?.emoji ?: ""
                        )
                    }

                    // 2. Category filters row (Spans all columns)
                    if (uiState.availableCategories.size > 1) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            CategoryChipsRow(
                                categories = uiState.availableCategories,
                                selectedCategory = uiState.selectedCategory,
                                onCategorySelected = { viewModel.setSelectedCategory(it) }
                            )
                        }
                    }

                    // 3. Grid elements
                    if (uiState.tasks.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            NoResultsState(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                onClearFilters = {
                                    viewModel.setSearchQuery("")
                                    viewModel.setSelectedCategory("All")
                                    isSearchActive = false
                                }
                            )
                        }
                    } else {
                        items(uiState.tasks, key = { it.task.id }) { taskWithLog ->
                            TaskCard(
                                taskWithLog = taskWithLog,
                                onClick = {
                                    viewModel.logTask(taskWithLog.task.id, taskWithLog.task.name)
                                },
                                onEdit = { onEditTask(taskWithLog.task.id) },
                                onDelete = { viewModel.deleteTask(taskWithLog.task) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    totalTodayLogs: Int,
    activeHabitsCount: Int,
    mostActiveHabitName: String?,
    mostActiveHabitEmoji: String,
    modifier: Modifier = Modifier
) {
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when (hour) {
        in 5..11 -> "Good Morning 🌅"
        in 12..16 -> "Good Afternoon ⚡"
        in 17..21 -> "Good Evening 🌙"
        else -> "Hello Night Owl 🦉"
    }

    Column(modifier = modifier.padding(bottom = 4.dp)) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Track your routine and stay consistent.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TODAY'S SUMMARY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "🔥 Consistency",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = totalTodayLogs.toString(),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 28.sp
                                ),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Total Logged Today",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = activeHabitsCount.toString(),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 28.sp
                                ),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Habits Checked",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }

                    if (!mostActiveHabitName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎯 Most active: $mostActiveHabitEmoji $mostActiveHabitName",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChipsRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onCategorySelected(category) }
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun NoResultsState(
    modifier: Modifier = Modifier,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🔍", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No matches found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Try adjusting your search terms or category filter.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onClearFilters,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Clear Filters")
        }
    }
}

@Composable
private fun EmptyDashboard(
    modifier: Modifier = Modifier,
    onAddTask: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "📋", fontSize = 52.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Start Tracking Habits",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Build routines, keep streaks, and analyze your productivity offline. Add your first habit button to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onAddTask,
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Create First Habit", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskCard(
    taskWithLog: TaskWithLastLog,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var animTrigger by remember { mutableStateOf(0) }
    val haptic = LocalHapticFeedback.current

    val isDark = isSystemInDarkTheme()
    val theme = remember(taskWithLog.task.category, isDark) {
        getCategoryTheme(taskWithLog.task.category, isDark)
    }

    val context = LocalContext.current
    val vibrator = remember(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = tween(100),
        label = "scale"
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Delete \"${taskWithLog.task.name}\"? All logs will also be removed.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp) // Fixed height to keep all cards identical in size!
                .background(Brush.verticalGradient(theme.gradientColors))
                .pointerInput(vibrator) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Press) {
                                vibrateTick(vibrator, isPress = true)
                            } else if (event.type == PointerEventType.Release) {
                                vibrateTick(vibrator, isPress = false)
                            }
                        }
                    }
                }
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = androidx.compose.foundation.LocalIndication.current,
                    onClick = {
                        animTrigger++
                        onClick()
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showMenu = true
                    }
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Row: Emoji Icon and Options Menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(theme.primaryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = taskWithLog.task.emoji, fontSize = 22.sp)
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = theme.primaryColor
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                leadingIcon = { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Task Name
                Text(
                    text = taskWithLog.task.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF263238),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(38.dp) // height constraint to keep cards aligned
                )

                // Category tag
                if (taskWithLog.task.category.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = theme.primaryColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = taskWithLog.task.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = theme.primaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Spacer weight to push all bottom content down to align cards equally
                Spacer(modifier = Modifier.weight(1f))

                // Counter & relative time row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${taskWithLog.todayCount}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                            color = theme.primaryColor
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "today",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }

                    taskWithLog.lastLoggedAt?.let { ts ->
                        Text(
                            text = formatRelativeTime(ts),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }

                // 7-day mini streak visualizer (Aligns exactly Monday to Sunday)
                if (taskWithLog.last7DaysLogged.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = theme.primaryColor.copy(alpha = 0.12f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val weekdays = listOf("M", "T", "W", "T", "F", "S", "S")
                        val todayIndex = remember {
                            val cal = Calendar.getInstance()
                            when (cal.get(Calendar.DAY_OF_WEEK)) {
                                Calendar.MONDAY -> 0
                                Calendar.TUESDAY -> 1
                                Calendar.WEDNESDAY -> 2
                                Calendar.THURSDAY -> 3
                                Calendar.FRIDAY -> 4
                                Calendar.SATURDAY -> 5
                                Calendar.SUNDAY -> 6
                                else -> 6
                            }
                        }

                        taskWithLog.last7DaysLogged.forEachIndexed { index, logged ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (logged) theme.primaryColor else theme.primaryColor.copy(alpha = 0.15f)
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = weekdays[index],
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        fontWeight = if (index == todayIndex) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (index == todayIndex) theme.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            // Plus One particle click animation
            PlusOneAnimation(
                triggerCount = animTrigger,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun PlusOneAnimation(
    triggerCount: Int,
    modifier: Modifier = Modifier
) {
    var animOffset by remember { mutableStateOf(0f) }
    var animAlpha by remember { mutableStateOf(0f) }

    LaunchedEffect(triggerCount) {
        if (triggerCount > 0) {
            animOffset = 0f
            animAlpha = 1f
            androidx.compose.animation.core.animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = androidx.compose.animation.core.EaseOutBack)
            ) { value, _ ->
                animOffset = -value * 120f
                animAlpha = 1f - value
            }
        }
    }

    if (animAlpha > 0f) {
        Text(
            text = "+1",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .graphicsLayer(
                    translationY = animOffset,
                    alpha = animAlpha
                )
        )
    }
}

private fun vibrateTick(vibrator: Vibrator, isPress: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val effectId = if (isPress) {
            VibrationEffect.EFFECT_HEAVY_CLICK
        } else {
            VibrationEffect.EFFECT_TICK
        }
        try {
            vibrator.vibrate(VibrationEffect.createPredefined(effectId))
        } catch (e: Exception) {
            fallbackVibrate(vibrator, isPress)
        }
    } else {
        fallbackVibrate(vibrator, isPress)
    }
}

private fun fallbackVibrate(vibrator: Vibrator, isPress: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val duration = if (isPress) 15L else 10L
        val amplitude = if (isPress) 220 else 150
        vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(if (isPress) 15L else 10L)
    }
}

private fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000L -> "just now"
        diff < 3_600_000L -> "${diff / 60_000}m ago"
        diff < 86_400_000L -> "${diff / 3_600_000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun getStartOfToday(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}
