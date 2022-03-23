package com.openclassrooms.realestatemanager.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RealEstateDao {

    @Insert
    suspend fun insert(realEstateEntity: RealEstateEntity)

    @Update
    suspend fun update(realEstateEntity: RealEstateEntity)

    @Query("SELECT * FROM RealEstateEntity")
    fun getAllRealEstates(): Flow<List<RealEstateEntity>>

    @Query("SELECT * FROM RealEstateEntity WHERE id = :estateId")
    fun getRealEstateById(estateId: Long): Flow<RealEstateEntity?>
}