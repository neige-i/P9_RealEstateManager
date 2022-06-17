package com.openclassrooms.realestatemanager.data

import androidx.annotation.StringRes

interface Localized {

    @get:StringRes
    val stringId: Int

    fun fromStringId(@StringRes stringId: Int): Localized
}