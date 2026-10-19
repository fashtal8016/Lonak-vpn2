package com.example.service

import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.core.content.ContextCompat
import com.example.core.data.local.entity.ServerEntity
import com.example.core.model.TrafficStats
import com.example.core.model.VpnStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TunnelController {

    private val _statusFlow = MutableStateFlow(VpnStatus.DISCONNECTED)
    val statusFlow: StateFlow<VpnStatus> = _statusFlow.asStateFlow()

    private val _trafficStats = MutableStateFlow(TrafficStats())
    val trafficStats: StateFlow<TrafficStats> = _trafficStats.asStateFlow()

    private val _activeServer = MutableStateFlow<ServerEntity?>(null)
    val activeServer: StateFlow<ServerEntity?> = _activeServer.asStateFlow()

    fun updateStatus(status: VpnStatus) {
        _statusFlow.value = status
    }

    fun updateStats(stats: TrafficStats) {
        _trafficStats.value = stats
    }

    fun updateActiveServer(server: ServerEntity?) {
        _activeServer.value = server
    }

    fun isVpnPrepared(context: Context): Boolean {
        return VpnService.prepare(context) == null
    }

    fun getVpnPrepareIntent(context: Context): Intent? {
        return VpnService.prepare(context)
    }

    fun connect(context: Context, serverId: Long? = null) {
        val intent = Intent(context, LonakVpnService::class.java).apply {
            action = LonakVpnService.ACTION_CONNECT
            if (serverId != null) {
                putExtra(LonakVpnService.EXTRA_SERVER_ID, serverId)
            }
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun disconnect(context: Context) {
        val intent = Intent(context, LonakVpnService::class.java).apply {
            action = LonakVpnService.ACTION_DISCONNECT
        }
        context.startService(intent)
    }
}
