package com.example.ui.servers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.local.entity.ServerEntity
import com.example.core.data.repository.ServerRepository
import com.example.core.model.ProtocolType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ServerSortOption(val titlePersian: String) {
    PING("کمترین پینگ"),
    SPEED("بیشترین سرعت"),
    STABILITY("پایدارترین"),
    NEWEST("جدیدترین")
}

class ServersViewModel(
    private val repository: ServerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedProtocol = MutableStateFlow<ProtocolType?>(null)
    val selectedProtocol: StateFlow<ProtocolType?> = _selectedProtocol.asStateFlow()

    private val _sortOption = MutableStateFlow(ServerSortOption.PING)
    val sortOption: StateFlow<ServerSortOption> = _sortOption.asStateFlow()

    val selectedServerId: StateFlow<Long?> = repository.selectedServerId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _testingServerId = MutableStateFlow<Long?>(null)
    val testingServerId: StateFlow<Long?> = _testingServerId.asStateFlow()

    private val _isTestingAll = MutableStateFlow(false)
    val isTestingAll: StateFlow<Boolean> = _isTestingAll.asStateFlow()

    val filteredServers: StateFlow<List<ServerEntity>> = combine(
        repository.allServers,
        _searchQuery,
        _selectedProtocol,
        _sortOption
    ) { servers, query, proto, sort ->
        var list = servers

        // Filter by protocol
        if (proto != null) {
            list = list.filter { it.protocol == proto }
        }

        // Filter by query
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                        it.countryName.lowercase().contains(q) ||
                        it.countryCode.lowercase().contains(q) ||
                        it.host.lowercase().contains(q) ||
                        it.protocol.displayName.lowercase().contains(q)
            }
        }

        // Sort
        when (sort) {
            ServerSortOption.PING -> list.sortedWith(
                compareByDescending<ServerEntity> { it.isFavorite }
                    .thenBy { if (it.pingMs > 0) 0 else 1 }
                    .thenBy { it.pingMs }
            )
            ServerSortOption.SPEED -> list.sortedWith(
                compareByDescending<ServerEntity> { it.isFavorite }
                    .thenByDescending { it.downloadSpeedMbps }
            )
            ServerSortOption.STABILITY -> list.sortedWith(
                compareByDescending<ServerEntity> { it.isFavorite }
                    .thenByDescending { it.successRate }
                    .thenByDescending { it.isCleanIp }
            )
            ServerSortOption.NEWEST -> list.sortedWith(
                compareByDescending<ServerEntity> { it.isFavorite }
                    .thenByDescending { it.id }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectProtocol(proto: ProtocolType?) {
        _selectedProtocol.value = proto
    }

    fun setSortOption(option: ServerSortOption) {
        _sortOption.value = option
    }

    fun selectServer(server: ServerEntity) {
        viewModelScope.launch {
            repository.setSelectedServer(server.id)
        }
    }

    fun toggleFavorite(server: ServerEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(server.id, !server.isFavorite)
        }
    }

    fun testServerPing(server: ServerEntity) {
        viewModelScope.launch {
            _testingServerId.value = server.id
            try {
                repository.testServerPing(server)
            } finally {
                _testingServerId.value = null
            }
        }
    }

    fun testAllPings() {
        if (_isTestingAll.value) return
        viewModelScope.launch {
            _isTestingAll.value = true
            try {
                repository.testAllServersPing()
            } finally {
                _isTestingAll.value = false
            }
        }
    }

    fun importConfig(text: String, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.importFromText(text)
            onResult(count)
        }
    }

    fun deleteServer(id: Long) {
        viewModelScope.launch {
            repository.deleteServer(id)
        }
    }
}
