package com.openclassrooms.realestatemanager.main

import androidx.annotation.IdRes

sealed class MainEvent

data class ShowDetailEvent(
    @IdRes val containerId: Int,
    val arg: String,
) : MainEvent()

object EndActivityEvent : MainEvent()

object GoBackEvent : MainEvent()
