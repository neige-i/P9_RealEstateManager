package com.openclassrooms.realestatemanager.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.data.RealEstateType
import java.time.LocalDate

@Entity
data class EstateEntity(
    @PrimaryKey(autoGenerate = true) val estateId: Long = 0,
    val type: RealEstateType,
    val price: Double?,
    val area: Int?,
    @ColumnInfo(name = "total_room_count") val totalRoomCount: Int,
    @ColumnInfo(name = "bathroom_count") val bathroomCount: Int,
    @ColumnInfo(name = "bedroom_count") val bedroomCount: Int,
    val description: String,
    @ColumnInfo(name = "street_name") val streetName: String,
    @ColumnInfo(name = "additional_address_info") val additionalAddressInfo: String,
    val city: String,
    val state: String,
    val zipcode: String,
    val country: String,
    @ColumnInfo(name = "market_entry_date") val marketEntryDate: LocalDate,
    @ColumnInfo(name = "sale_date") val saleDate: LocalDate?,
)
