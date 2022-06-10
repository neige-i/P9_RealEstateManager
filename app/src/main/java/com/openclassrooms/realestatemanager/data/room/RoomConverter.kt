package com.openclassrooms.realestatemanager.data.room

import androidx.room.TypeConverter
import com.openclassrooms.realestatemanager.data.UtilsRepository
import java.time.LocalDate

object RoomConverter {

    @TypeConverter
    fun dateToString(date: LocalDate?): String? = date?.format(UtilsRepository.DATE_FORMATTER)

    @TypeConverter
    fun stringToDate(s: String?): LocalDate? = s?.let { LocalDate.parse(it, UtilsRepository.DATE_FORMATTER) }
}