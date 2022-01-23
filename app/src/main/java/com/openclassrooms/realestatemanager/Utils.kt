package com.openclassrooms.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utils {

    // CURRENCY

    private const val USD_EUR = .8815 // Currency rate on January, 19th 2022

    private fun Double.toCurrency(): BigDecimal = toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)

    fun convertDollarToEuro(usd: Double): BigDecimal = (usd * USD_EUR).toCurrency()

    fun convertEuroToDollar(eur: Double): BigDecimal = (eur / USD_EUR).toCurrency()

    // DATE

    fun todayDate(clock: Clock): String {
        return LocalDate.now(clock).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    // NETWORK CONNECTION

    fun isInternetAvailable(context: Context, listener: (Boolean) -> Unit) {
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
                listener(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                listener(false)
            }
        }

        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .requestNetwork(networkRequest, networkCallback)
    }
}