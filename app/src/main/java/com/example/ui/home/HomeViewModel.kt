package com.example.ui.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.local.entity.ServerEntity
import com.example.core.data.repository.ServerRepository
import com.example.core.model.TrafficStats
import com.example.core.model.VpnStatus
import com.example.service.TunnelController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ServerRepository
) : ViewModel() {

    val vpnStatus: StateFlow<VpnStatus> = TunnelController.statusFlow
    val trafficStats: StateFlow<TrafficStats> = TunnelController.trafficStats

    val activeServer: StateFlow<ServerEntity?> = combine(
        TunnelController.activeServer,
        repository.selectedServerId,
        repository.allServers
    ) { connected, selectedId, allServers ->
        if (connected != null) return@combine connected
        if (selectedId != null) {
            allServers.find { it.id == selectedId }
        } else {
            allServers.minByOrNull { if (it.pingMs > 0) it.pingMs else 9999 } ?: allServers.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            repository.seedInitialServersIfNeeded()
        }
    }

    fun toggleVpn(context: Context, onPrepareRequired: (Intent) -> Unit) {
        val currentStatus = vpnStatus.value
        if (currentStatus == VpnStatus.CONNECTED || currentStatus == VpnStatus.CONNECTING) {
            TunnelController.disconnect(context)
        } else {
            val prepareIntent = TunnelController.getVpnPrepareIntent(context)
            if (prepareIntent != null) {
                onPrepareRequired(prepareIntent)
            } else {
                startConnect(context)
            }
        }
    }

    fun onVpnPermissionGranted(context: Context) {
        startConnect(context)
    }

    private fun startConnect(context: Context) {
        val server = activeServer.value
        TunnelController.connect(context, server?.id)
    }

    fun quickConnect(context: Context, onPrepareRequired: (Intent) -> Unit) {
        viewModelScope.launch {
            val best = repository.getBestServer()
            if (best != null) {
                repository.setSelectedServer(best.id)
            }
            toggleVpn(context, onPrepareRequired)
        }
    }
}
