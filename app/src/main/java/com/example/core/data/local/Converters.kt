package com.example.core.data.local

import androidx.room.TypeConverter
import com.example.core.model.ProtocolType

class Converters {
    @TypeConverter
    fun fromProtocolType(value: ProtocolType): String {
        return value.name
    }

    @TypeConverter
    fun toProtocolType(value: String): ProtocolType {
        return try {
            ProtocolType.valueOf(value)
        } catch (e: Exception) {
            ProtocolType.VLESS
        }
    }
}
