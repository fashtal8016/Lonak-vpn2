package com.example.core.data.repository

import com.example.core.data.local.dao.LogDao
import com.example.core.data.local.dao.ServerDao
import com.example.core.data.local.entity.LogEntity
import com.example.core.data.local.entity.ServerEntity
import com.example.core.data.preferences.AppPreferences
import com.example.core.model.VpnSettings
import com.example.core.parser.UriParser
import com.example.core.scanner.ServerScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class ServerRepository(
    private val serverDao: ServerDao,
    private val logDao: LogDao,
    private val preferences: AppPreferences,
    private val scraper: ServerScraper
) {
    val allServers: Flow<List<ServerEntity>> = serverDao.getAllServersFlow()
    val recentLogs: Flow<List<LogEntity>> = logDao.getRecentLogsFlow()
    val settings: Flow<VpnSettings> = preferences.settings
    val selectedServerId: Flow<Long?> = preferences.selectedServerId

    suspend fun seedInitialServersIfNeeded() = withContext(Dispatchers.IO) {
        val count = serverDao.getCount()
        if (count == 0) {
            val pristine = scraper.getFallbackPristineServers()
            serverDao.insertAll(pristine)
            logDao.insertLog(
                LogEntity(
                    tag = "INIT",
                    message = "Loaded ${pristine.size} verified Iranian anti-filtering nodes",
                    level = "INFO"
                )
            )
            // Select the fastest server as default
            val best = pristine.minByOrNull { it.pingMs }
            best?.let {
                preferences.setSelectedServerId(it.id)
            }
        }
    }

    suspend fun getServerById(id: Long): ServerEntity? = withContext(Dispatchers.IO) {
        serverDao.getServerById(id)
    }

    suspend fun getBestServer(): ServerEntity? = withContext(Dispatchers.IO) {
        serverDao.getBestServer()
    }

    suspend fun insertServer(server: ServerEntity): Long = withContext(Dispatchers.IO) {
        serverDao.insert(server)
    }

    suspend fun deleteServer(id: Long) = withContext(Dispatchers.IO) {
        serverDao.deleteById(id)
    }

    suspend fun deleteAllServers() = withContext(Dispatchers.IO) {
        serverDao.deleteAll()
    }

    suspend fun toggleFavorite(id: Long, isFav: Boolean) = withContext(Dispatchers.IO) {
        serverDao.toggleFavorite(id, isFav)
    }

    suspend fun testServerPing(server: ServerEntity): ServerEntity = withContext(Dispatchers.IO) {
        scraper.testSingleServerPing(server)
    }

    suspend fun testAllServersPing() = withContext(Dispatchers.IO) {
        val servers = allServers.firstOrNull() ?: emptyList()
        for (server in servers.take(20)) {
            scraper.testSingleServerPing(server)
        }
    }

    suspend fun importFromText(rawText: String): Int = withContext(Dispatchers.IO) {
        val parsedList = UriParser.parseSubscriptionContent(rawText)
        if (parsedList.isNotEmpty()) {
            serverDao.insertAll(parsedList)
            logDao.insertLog(
                LogEntity(
                    tag = "IMPORT",
                    message = "Imported ${parsedList.size} new configurations",
                    level = "INFO"
                )
            )
        }
        parsedList.size
    }

    suspend fun clearLogs() = withContext(Dispatchers.IO) {
        logDao.clearLogs()
    }

    suspend fun setSelectedServer(id: Long?) = withContext(Dispatchers.IO) {
        preferences.setSelectedServerId(id)
    }

    suspend fun updateSettings(update: suspend (AppPreferences) -> Unit) = withContext(Dispatchers.IO) {
        update(preferences)
    }
}
