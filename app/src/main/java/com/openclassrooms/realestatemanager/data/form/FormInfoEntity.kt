package com.openclassrooms.realestatemanager.data.form

data class FormInfoEntity(
    val form: FormEntity,
    val pageCount: Int,
    val type: FormType,
    val hasModifications: Boolean,
) {

    enum class FormType {
        ADD,
        EDIT,
    }
}
