package com.openclassrooms.realestatemanager.data.room

import androidx.room.Dao
import androidx.room.Insert
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity

@Dao
interface RealEstateDao {

    @Insert
    suspend fun insert(realEstateEntity: RealEstateEntity)
}