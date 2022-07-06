package com.openclassrooms.realestatemanager.data.room

import androidx.room.*
import com.openclassrooms.realestatemanager.data.real_estate.RealEstateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Insert
    suspend fun insertEstate(estate: EstateEntity): Long

    @Update
    suspend fun updateEstate(estate: EstateEntity)

    @Transaction
    @Query("SELECT * FROM EstateEntity")
    fun getAllRealEstates(): Flow<List<RealEstateEntity>>

    @Transaction
    @Query("SELECT * FROM EstateEntity WHERE estateId = :estateId")
    fun getRealEstateById(estateId: Long): Flow<RealEstateEntity?>

    // ----------

    @Insert
    suspend fun insertPhoto(photo: PhotoEntity)

    @Update
    suspend fun updatePhoto(photo: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    // ----------

    @Insert
    suspend fun insertPoi(poi: PoiEntity)

    @Insert
    suspend fun insertEstatePoiCrossRef(estatePoiCrossRef: EstatePoiCrossRef)

    @Delete
    suspend fun updateEstatePoiCrossRef(estatePoiCrossRef: EstatePoiCrossRef)

    // ----------

    @Query("SELECT * FROM AgentEntity")
    fun getAllAgents(): Flow<List<AgentEntity>>

    @Insert
    suspend fun insertAgent(agent: AgentEntity)
}