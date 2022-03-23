package com.openclassrooms.realestatemanager.data.real_estate

import com.openclassrooms.realestatemanager.data.room.RealEstateDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RealEstateRepository @Inject constructor(
    private val realEstateDao: RealEstateDao,
) {

    suspend fun createRealEstate(realEstateEntity: RealEstateEntity) {
        realEstateDao.insert(realEstateEntity)
    }

    suspend fun updateRealEstate(realEstateEntity: RealEstateEntity) {
        realEstateDao.update(realEstateEntity)
    }

    fun getAllEstates(): Flow<List<RealEstateEntity>> = realEstateDao.getAllRealEstates()

    fun getEstate(id: Long): Flow<RealEstateEntity?> = realEstateDao.getRealEstateById(id)
}