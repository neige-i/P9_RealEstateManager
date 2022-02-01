package com.openclassrooms.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.util.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Assert.assertFalse
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

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val connectivityManagerShadow: ShadowConnectivityManager = shadowOf(
        RuntimeEnvironment
            .getApplication()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    )
    private val utilsRepository = UtilsRepository(
        Clock.fixed(
            LocalDate.of(1977, 5, 4).atStartOfDay().toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
        ),
        RuntimeEnvironment.getApplication(),
        testCoroutineRule.testCoroutineScope,
        testCoroutineRule.testCoroutineDispatcher,
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
    fun `return May, 4th 1977 in the correct format`() {
        // WHEN
        val todayDate = utilsRepository.todayDate()

        // THEN
        Assert.assertEquals("04/05/1977", todayDate)
    }

    @Test
    fun `return true when network is available`() {
        // GIVEN
        var isNetworkAvailable: Boolean? = null

        // WHEN
        utilsRepository.isInternetAvailable().observeForever {
            isNetworkAvailable = it
        }

        // TRIGGER CALLBACK
        connectivityManagerShadow.networkCallbacks.first().onAvailable(ShadowNetwork.newInstance(1))

        // THEN
        assertTrue(isNetworkAvailable!!)
    }

    @Test
    fun `return false when network is lost`() {
        // GIVEN
        var isNetworkAvailable: Boolean? = null

        // WHEN
        utilsRepository.isInternetAvailable().observeForever { isNetworkAvailable = it }

        // TRIGGER CALLBACK
        connectivityManagerShadow.networkCallbacks.first().onLost(ShadowNetwork.newInstance(1))

        // THEN
        assertFalse(isNetworkAvailable!!)
    }
}