package com.openclassrooms.realestatemanager.ui.util

import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData

inline fun <T> MutableLiveData<T>.update(function: (T?) -> T) {
    value = function(value)
}

fun Toolbar.setNavigationIcon(@DrawableRes iconId: Int?) {
    navigationIcon = iconId?.let { ContextCompat.getDrawable(context, it) }
}