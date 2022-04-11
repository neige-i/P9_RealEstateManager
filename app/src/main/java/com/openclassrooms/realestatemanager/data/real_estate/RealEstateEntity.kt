package com.openclassrooms.realestatemanager.data.real_estate

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.openclassrooms.realestatemanager.data.room.*

data class RealEstateEntity(
    @Embedded val info: EstateEntity,
    @Relation(
        parentColumn = "realEstateId",
        entityColumn = "estateId"
    )
    val photoList: List<PhotoEntity>,
    @Relation(
        parentColumn = "realEstateId",
        entityColumn = "poiValue",
        associateBy = Junction(EstatePoiCrossRef::class)
    )
    val poiList: List<PoiEntity>,
    @Relation(
        parentColumn = "realEstateId",
        entityColumn = "username",
        associateBy = Junction(EstateAgentCrossRef::class)
    )
    val agent: AgentEntity?,
)
