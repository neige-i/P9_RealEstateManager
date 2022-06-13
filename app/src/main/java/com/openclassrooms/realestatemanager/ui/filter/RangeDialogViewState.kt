package com.openclassrooms.realestatemanager.ui.filter

import android.util.Range
import androidx.annotation.StringRes

data class RangeDialogViewState(
    val style: Style,
    val selection: Range<Float>,
    val bounds: Range<Float>,
) {

    data class Style(
        @StringRes val title: Int,
        val label: String,
        val step: Float,
    )
}
