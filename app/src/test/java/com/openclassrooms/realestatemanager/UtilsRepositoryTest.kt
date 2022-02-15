package com.openclassrooms.realestatemanager

import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.util.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowConnectivityManager
import org.robolectric.shadows.ShadowNetwork
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class UtilsRepositoryTest {

    companion object {
        // IN
        private const val USD = 42.42 // On January, 19th 2022: $42.42 = €37.39
        private const val EUR = 37.39

        // OUT
        private val USD_BIG = BigDecimal(42.42).setScale(2, RoundingMode.HALF_EVEN)
        private val EUR_BIG = BigDecimal(37.39).setScale(2, RoundingMode.HALF_EVEN)
    }

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val shadowConnectivityManager: ShadowConnectivityManager = shadowOf(
        RuntimeEnvironment.getApplication().getSystemService<ConnectivityManager>()
    )

    private val utilsRepository = UtilsRepository(
        Clock.fixed(
            LocalDate.of(1977, 5, 4).atStartOfDay().toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
        ),
        RuntimeEnvironment.getApplication(),
    )

    @Test
    fun `convert USD to EUR`() {
        // WHEN
        val euros = utilsRepository.convertDollarToEuro(USD)

        // THEN
        Assert.assertEquals(EUR_BIG, euros)
    }

    @Test
    fun `convert EUR to USD`() {
        // WHEN
        val dollars = utilsRepository.convertEuroToDollar(EUR)

        // THEN
        Assert.assertEquals(USD_BIG, dollars)
    }

    @Test
    fun `return May, 4th 1977 in the correct String format`() {
        // WHEN
        val todayDate = utilsRepository.todayDate()

        // THEN
        Assert.assertEquals("04/05/1977", todayDate)
    }

    @Test
    fun `return August, 6th 1991 from the String`() {
        // WHEN
        val parsedDate = UtilsRepository.stringToDate("06/08/1991")

        // THEN
        Assert.assertEquals(LocalDate.of(1991, 8, 6), parsedDate)
    }

    @Test
    fun `return true when network is available`() = runTest {
        // GIVEN
        var isAvailable: Boolean? = null

        // WHEN
        // Launch the flow in a new coroutine
        // Otherwise, the rest of the test is not executed and the test runs forever
        launch { isAvailable = utilsRepository.isInternetAvailable().first() }

        // TRIGGER CALLBACK
        // Wait for UtilsRepository.isInternetAvailable() to initialize the NetworkCallback
        // Otherwise, ShadowConnectivityManager.networkCallbacks returns an empty set
        advanceUntilIdle()
        shadowConnectivityManager.networkCallbacks.onEach {
            it.onAvailable(ShadowNetwork.newInstance(1))
        }

        // THEN
        // Wait for the coroutine to complete when the flow is collected with Flow.first(),
        // Otherwise, available is null
        advanceUntilIdle()
        assertTrue(isAvailable == true)
    }

    @Test
    fun `return false when network is lost`() = runTest {
        // GIVEN
        var isAvailable: Boolean? = null

        // WHEN
        // Launch the flow in a new coroutine
        // Otherwise, the rest of the test is not executed and the test runs forever
        launch { isAvailable = utilsRepository.isInternetAvailable().first() }

        // TRIGGER CALLBACK
        // Wait for UtilsRepository.isInternetAvailable() to initialize the NetworkCallback
        // Otherwise, ShadowConnectivityManager.networkCallbacks returns an empty set
        advanceUntilIdle()
        shadowConnectivityManager.networkCallbacks.onEach {
            it.onLost(ShadowNetwork.newInstance(1))
        }

        // THEN
        // Wait for the coroutine to complete by collecting the flow with first(),
        // Otherwise, available is null
        advanceUntilIdle()
        assertTrue(isAvailable == false)
    }
}