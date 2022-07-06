package com.openclassrooms.realestatemanager.ui.filter.slider

import android.util.Range
import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.ui.util.LocalText

data class SliderViewState(
    val style: Style,
    val selection: Range<Float>,
    val bounds: Range<Float>,
) {

    data class Style(
        @StringRes val dialogTitle: Int,
        val label: LocalText,
        val step: Float,
    )
}
