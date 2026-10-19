package com.example.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.core.data.local.entity.ServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {

    @Query("SELECT * FROM servers ORDER BY isFavorite DESC, CASE WHEN pingMs > 0 THEN 0 ELSE 1 END, pingMs ASC, id DESC")
    fun getAllServersFlow(): Flow<List<ServerEntity>>

    @Query("SELECT * FROM servers WHERE id = :id LIMIT 1")
    suspend fun getServerById(id: Long): ServerEntity?

    @Query("SELECT * FROM servers WHERE id = :id LIMIT 1")
    fun getServerByIdFlow(id: Long): Flow<ServerEntity?>

    @Query("SELECT * FROM servers WHERE pingMs > 0 ORDER BY isCleanIp DESC, pingMs ASC LIMIT 1")
    suspend fun getBestServer(): ServerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(server: ServerEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(servers: List<ServerEntity>): List<Long>

    @Update
    suspend fun update(server: ServerEntity)

    @Query("UPDATE servers SET pingMs = :pingMs, isCleanIp = :isCleanIp, tag = :tag, lastTestedAt = :testedAt WHERE id = :id")
    suspend fun updatePing(id: Long, pingMs: Int, isCleanIp: Boolean, tag: String, testedAt: Long)

    @Query("UPDATE servers SET isFavorite = :isFav WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFav: Boolean)

    @Delete
    suspend fun delete(server: ServerEntity)

    @Query("DELETE FROM servers WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM servers")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM servers")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM servers WHERE isCleanIp = 1")
    suspend fun getCleanIpCount(): Int
}
