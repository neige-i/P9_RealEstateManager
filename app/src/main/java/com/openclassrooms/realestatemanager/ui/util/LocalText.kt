package com.openclassrooms.realestatemanager.ui.util

import android.content.Context
import androidx.annotation.StringRes

sealed class LocalText {
    data class Simple(val content: String) : LocalText()
    data class Res(@StringRes val stringId: Int) : LocalText()
    data class ResWithArgs(@StringRes val stringId: Int, val args: List<Any>) : LocalText()
    data class ResWithRes(@StringRes val stringId: Int, val resArgs: List<Int>) : LocalText()
}

fun LocalText.toCharSequence(context: Context): CharSequence = when (this) {
    is LocalText.Res -> context.getString(stringId)
    is LocalText.ResWithArgs -> context.getString(stringId, args)
    is LocalText.ResWithRes -> context.getString(stringId, resArgs.map { context.getString(it) })
    is LocalText.Simple -> content
}
