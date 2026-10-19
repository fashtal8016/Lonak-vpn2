package com.example.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.core.data.local.entity.LogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {

    @Query("SELECT * FROM system_logs ORDER BY timestamp DESC LIMIT 200")
    fun getRecentLogsFlow(): Flow<List<LogEntity>>

    @Insert
    suspend fun insertLog(log: LogEntity)

    @Query("DELETE FROM system_logs")
    suspend fun clearLogs()

    @Query("DELETE FROM system_logs WHERE id NOT IN (SELECT id FROM system_logs ORDER BY timestamp DESC LIMIT 300)")
    suspend fun pruneOldLogs()
}
