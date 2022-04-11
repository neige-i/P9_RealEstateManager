package com.openclassrooms.realestatemanager.data.real_estate

import com.openclassrooms.realestatemanager.data.room.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealEstateRepository @Inject constructor(
    private val roomDao: RoomDao,
) {

    fun getAllRealEstates(): Flow<List<RealEstateEntity>> = roomDao.getAllRealEstates()

    fun getRealEstate(id: Long): Flow<RealEstateEntity?> = roomDao.getRealEstateById(id)

    // ----------

    suspend fun addEstate(estate: EstateEntity): Long = roomDao.insertEstate(estate)

    suspend fun setEstate(estate: EstateEntity) {
        roomDao.updateEstate(estate)
    }

    // ----------

    suspend fun addPhoto(photo: PhotoEntity) {
        roomDao.insertPhoto(photo)
    }

    suspend fun setPhoto(photo: PhotoEntity) {
        roomDao.updatePhoto(photo)
    }

    suspend fun removePhoto(photo: PhotoEntity) {
        roomDao.deletePhoto(photo)
    }

    // ----------

    suspend fun addEstatePoiRef(estatePoiCrossRef: EstatePoiCrossRef) {
        roomDao.insertEstatePoiCrossRef(estatePoiCrossRef)
    }

    suspend fun removeEstatePoiRef(estatePoiCrossRef: EstatePoiCrossRef) {
        roomDao.updateEstatePoiCrossRef(estatePoiCrossRef)
    }

    // ----------

    fun getAllAgents(): Flow<List<AgentEntity>> = roomDao.getAllAgents()

    fun isEstateTakenCareByAgent(estateId: Long): Boolean = roomDao.estateWithAgent(estateId)

    suspend fun addEstateAgentRef(estateAgentRef: EstateAgentCrossRef) {
        roomDao.insertEstateAgentCrossRef(estateAgentRef)
    }

    suspend fun setEstateAgentRef(estateAgentRef: EstateAgentCrossRef) {
        roomDao.updateEstateAgentCrossRef(estateAgentRef)
    }
}