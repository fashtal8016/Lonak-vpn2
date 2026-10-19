package com.example.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.repository.ServerRepository
import com.example.core.model.DnsProvider
import com.example.core.model.VpnSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: ServerRepository
) : ViewModel() {

    val settings: StateFlow<VpnSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VpnSettings())

    fun updateDnsProvider(provider: DnsProvider) {
        viewModelScope.launch {
            repository.updateSettings { it.updateDnsProvider(provider) }
        }
    }

    fun setAdBlockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.setAdBlockEnabled(enabled) }
        }
    }

    fun setKillSwitchEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.setKillSwitchEnabled(enabled) }
        }
    }

    fun setAutoReconnect(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.setAutoReconnect(enabled) }
        }
    }

    fun setSplitTunnelingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.setSplitTunnelingEnabled(enabled) }
        }
    }

    fun setPreferredCore(core: String) {
        viewModelScope.launch {
            repository.updateSettings { it.setPreferredCore(core) }
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            repository.updateSettings { it.setLanguage(lang) }
        }
    }

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.setDarkMode(isDark) }
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.deleteAllServers()
            repository.clearLogs()
            repository.seedInitialServersIfNeeded()
        }
    }
}
