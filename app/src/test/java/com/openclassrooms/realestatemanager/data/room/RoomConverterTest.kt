package com.openclassrooms.realestatemanager.data.room

import org.junit.Assert.assertEquals
import org.junit.Test

class RoomConverterTest {

    private val roomConverter = RoomConverter

    @Test
    fun `return non-empty list when string is not empty`() {
        // WHEN
        val resultList = roomConverter.stringToList("1, 2, 3, 4")

        // THEN
        assertEquals(listOf("1", "2", "3", "4"), resultList)
    }

    @Test
    fun `return empty list when string is empty`() {
        // WHEN
        val resultList = roomConverter.stringToList("")

        // THEN
        assertEquals(emptyList<String>(), resultList)
    }

    @Test
    fun `return non-empty String when list is not empty`() {
        // WHEN
        val resultString = roomConverter.listToString(listOf("1", "2", "3", "4"))

        // THEN
        assertEquals("1, 2, 3, 4", resultString)
    }

    @Test
    fun `return empty String when list is empty`() {
        // WHEN
        val resultString = roomConverter.listToString(emptyList())

        // THEN
        assertEquals("", resultString)
    }
}