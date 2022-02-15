package com.openclassrooms.realestatemanager.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UtilsRepository @Inject constructor(
    private val clock: Clock,
    @ApplicationContext private val context: Context,
) {

    companion object {
        val STATE_POSTAL_ABBR = listOf(
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL",
            "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE",
            "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "PR", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "VI", "WA", "WV", "WI", "WY",
        )

        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        fun stringToDate(text: String): LocalDate = LocalDate.parse(text, DATE_FORMATTER)

        // Currency rate on January, 19th 2022
        private const val USD_EUR = .8815
    }

    // CURRENCY

    fun convertDollarToEuro(usd: Double): BigDecimal = toCurrency(usd * USD_EUR)

    fun convertEuroToDollar(eur: Double): BigDecimal = toCurrency(eur / USD_EUR)

    private fun toCurrency(price: Double): BigDecimal {
        return price.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
    }

    // DATE

    fun todayDate(): String = LocalDate.now(clock).format(DATE_FORMATTER)

    // NETWORK CONNECTION

    fun isInternetAvailable(): Flow<Boolean> = callbackFlow {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(false)
            }
        }

        val connectivityManager = context.getSystemService<ConnectivityManager>()

        connectivityManager?.requestNetwork(networkRequest, networkCallback)

        awaitClose { connectivityManager?.unregisterNetworkCallback(networkCallback) }
    }
}