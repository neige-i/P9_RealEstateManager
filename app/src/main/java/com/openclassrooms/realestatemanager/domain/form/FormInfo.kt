package com.openclassrooms.realestatemanager.domain.form

import com.openclassrooms.realestatemanager.data.RealEstateType

data class FormInfo(
    val formType: FormType,
    val isModified: Boolean,
    val estateType: RealEstateType?,
)
