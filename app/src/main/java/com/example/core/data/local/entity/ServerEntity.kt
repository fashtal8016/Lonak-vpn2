package com.example.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.core.model.ProtocolType

@Entity(
    tableName = "servers",
    indices = [
        Index(value = ["rawUri"], unique = true),
        Index(value = ["pingMs"]),
        Index(value = ["countryCode"])
    ]
)
data class ServerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val protocol: ProtocolType,
    val host: String,
    val port: Int,
    val rawUri: String,
    val configJson: String = "",
    val countryCode: String = "DE",
    val countryName: String = "Germany",
    val pingMs: Int = -1, // -1 means untested, >=0 is ms
    val isCleanIp: Boolean = false,
    val tag: String = "NEW", // "FAST", "STABLE", "TESTED", "NEW"
    val downloadSpeedMbps: Float = 0f,
    val uploadSpeedMbps: Float = 0f,
    val lastTestedAt: Long = 0L,
    val isFavorite: Boolean = false,
    val subscriptionUrl: String? = null,
    val successRate: Int = 100
) {
    fun getFlagEmoji(): String {
        if (countryCode.length != 2) return "🌐"
        val firstChar = Character.codePointAt(countryCode.uppercase(), 0) - 0x41 + 0x1F1E6
        val secondChar = Character.codePointAt(countryCode.uppercase(), 1) - 0x41 + 0x1F1E6
        return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
    }
}
