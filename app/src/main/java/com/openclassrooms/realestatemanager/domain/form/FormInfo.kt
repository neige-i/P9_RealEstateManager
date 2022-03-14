package com.openclassrooms.realestatemanager.domain.form

data class FormInfo(
    val formType: FormType,
    val isModified: Boolean,
    val estateType: String,
) {

    enum class FormType {
        ADD,
        EDIT,
    }
}
