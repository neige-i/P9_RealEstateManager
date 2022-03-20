package com.openclassrooms.realestatemanager.data.real_estate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RealEstateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val price: Double?,
    val area: Int?,
    @ColumnInfo(name = "total_room_count") val totalRoomCount: Int,
    @ColumnInfo(name = "bathroom_count") val bathroomCount: Int,
    @ColumnInfo(name = "bedroom_count") val bedroomCount: Int,
    val description: String,
    @ColumnInfo(name = "picture_list") val pictureList: Map<String, String>,
    @ColumnInfo(name = "street_name") val streetName: String,
    @ColumnInfo(name = "additional_address_info") val additionalAddressInfo: String,
    val city: String,
    val state: String,
    val zipcode: String,
    val country: String,
    @ColumnInfo(name = "points_of_interests") val pointsOfInterests: List<String>,
    @ColumnInfo(name = "agent_id") val agentId: String?,
    @ColumnInfo(name = "market_entry_date") val marketEntryDate: String,
    @ColumnInfo(name = "sale_date") val saleDate: String?,
)
