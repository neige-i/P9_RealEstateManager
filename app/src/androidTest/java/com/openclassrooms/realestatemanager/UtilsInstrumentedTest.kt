package com.openclassrooms.realestatemanager

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test

class UtilsInstrumentedTest {

    @Test
    fun returnIfConnected() {
        // WHEN
        Utils.isInternetAvailable(InstrumentationRegistry.getInstrumentation().targetContext) {

            // THEN
            assertTrue(it)
        }
    }
}