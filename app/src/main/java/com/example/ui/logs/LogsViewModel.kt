package com.example.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.local.entity.LogEntity
import com.example.core.data.repository.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LogsViewModel(
    private val repository: ServerRepository
) : ViewModel() {

    private val _selectedLevel = MutableStateFlow("ALL")
    val selectedLevel: StateFlow<String> = _selectedLevel.asStateFlow()

    val logs: StateFlow<List<LogEntity>> = combine(
        repository.recentLogs,
        _selectedLevel
    ) { allLogs, level ->
        if (level == "ALL") allLogs else allLogs.filter { it.level.equals(level, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setLevel(level: String) {
        _selectedLevel.value = level
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }
}
