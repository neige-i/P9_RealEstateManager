package com.openclassrooms.realestatemanager.data.room

import androidx.room.TypeConverter

object RoomConverter {

    @TypeConverter
    fun stringToList(s: String): List<String> = s.split(", ")

    @TypeConverter
    fun listToString(list: List<String>): String = list.joinToString()
}