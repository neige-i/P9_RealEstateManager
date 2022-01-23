package com.openclassrooms.realestatemanager

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

class UtilsUnitTest {

    companion object {
        // IN
        private const val USD = 42.42 // On January, 19th 2022: $42.42 = €37.39
        private const val EUR = 37.39

        // OUT
        private val USD_BIG = BigDecimal(42.42).setScale(2, RoundingMode.HALF_EVEN)
        private val EUR_BIG = BigDecimal(37.39).setScale(2, RoundingMode.HALF_EVEN)
    }

    @Test
    fun `convert USD to EUR`() {
        // WHEN
        val euros = Utils.convertDollarToEuro(USD)

        // THEN
        assertEquals(EUR_BIG, euros)
    }

    @Test
    fun `convert EUR to USD`() {
        // WHEN
        val dollars = Utils.convertEuroToDollar(EUR)

        // THEN
        assertEquals(USD_BIG, dollars)
    }

    @Test
    fun `return May, 4th 1977 in the correct format`() {
        // WHEN
        val todayDate = Utils.todayDate(LocalDate.of(1977, 5, 4).toClock())

        // THEN
        assertEquals("04/05/1977", todayDate)
    }

    private fun LocalDate.toClock(): Clock = Clock.fixed(
        this.atStartOfDay().toInstant(ZoneOffset.UTC),
        ZoneOffset.UTC
    )
}