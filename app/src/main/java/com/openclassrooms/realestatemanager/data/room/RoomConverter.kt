package com.openclassrooms.realestatemanager.data.room

import androidx.room.TypeConverter

object RoomConverter {

    @TypeConverter
    fun stringToList(s: String): List<String> = if (s.isNotEmpty()) s.split(", ") else emptyList()

    @TypeConverter
    fun listToString(list: List<String>): String = list.joinToString()

    @TypeConverter
    fun stringToMap(s: String): Map<String, String> = if (s.isNotEmpty()) {
        s.removePrefix("{")
            .removeSuffix("}")
            .split(", ")
            .associate { keyValue ->
                keyValue.split("=").let {
                    it[0] to it[1]
                }
            }
    } else {
        emptyMap()
    }

    @TypeConverter
    fun mapToString(map: Map<String, String>) = map.toString()
}