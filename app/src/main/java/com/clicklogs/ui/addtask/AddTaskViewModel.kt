package com.clicklogs.ui.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clicklogs.data.db.entity.Task
import com.clicklogs.data.repository.ClickLogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddTaskUiState(
    val name: String = "",
    val category: String = "",
    val emoji: String = "📌",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val nameError: String? = null
)

class AddTaskViewModel(
    private val repository: ClickLogsRepository,
    private val editTaskId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    init {
        if (editTaskId != null) {
            loadTask(editTaskId)
        }
    }

    private fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val task = repository.getTaskById(taskId)
            if (task != null) {
                _uiState.value = AddTaskUiState(
                    name = task.name,
                    category = task.category,
                    emoji = task.emoji,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, nameError = null)
    }

    fun onCategoryChange(value: String) {
        _uiState.value = _uiState.value.copy(category = value)
    }

    fun onEmojiChange(value: String) {
        _uiState.value = _uiState.value.copy(emoji = value)
    }

    fun saveTask() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.value = state.copy(nameError = "Task name is required")
            return
        }
        viewModelScope.launch {
            if (editTaskId != null) {
                val existing = repository.getTaskById(editTaskId)
                if (existing != null) {
                    repository.updateTask(
                        existing.copy(
                            name = state.name.trim(),
                            category = state.category.trim(),
                            emoji = state.emoji
                        )
                    )
                }
            } else {
                repository.insertTask(
                    Task(
                        name = state.name.trim(),
                        category = state.category.trim(),
                        emoji = state.emoji
                    )
                )
            }
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    class Factory(
        private val repository: ClickLogsRepository,
        private val editTaskId: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddTaskViewModel(repository, editTaskId) as T
        }
    }
}
