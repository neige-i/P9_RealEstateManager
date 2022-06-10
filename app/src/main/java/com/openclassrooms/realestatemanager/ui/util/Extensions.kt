package com.openclassrooms.realestatemanager.ui.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.data.UtilsRepository
import java.time.LocalDate

inline fun TextView.onAfterTextChanged(
    crossinline listener: (
        text: Editable?,
        cursorPosition: Int
    ) -> Unit
): TextWatcher = object : TextWatcher {

    private var cursorPosition = 0

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        cursorPosition = start + after
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        listener(s, cursorPosition)
    }
}.apply {
    addTextChangedListener(this)
}

fun String.toLocalDate(): LocalDate = LocalDate.parse(this, UtilsRepository.DATE_FORMATTER)

inline fun <T> MutableLiveData<T>.update(function: (T?) -> T) {
    value.let { value = function(it) }
}

fun Toolbar.setNavigationIcon(@DrawableRes iconId : Int?) {
    navigationIcon = if (iconId != null) {
        ContextCompat.getDrawable(context, iconId)
    } else {
        null
    }
}