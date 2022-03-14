package com.openclassrooms.realestatemanager.ui.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CoroutineProvider @Inject constructor() {

    fun getIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    fun getMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}