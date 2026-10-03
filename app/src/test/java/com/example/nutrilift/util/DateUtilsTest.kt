package com.example.nutrilift.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {
    @Test
    fun validatesIsoDates() {
        assertTrue(DateUtils.isValidDate("2026-07-06"))
        assertFalse(DateUtils.isValidDate("06-07-2026"))
    }

    @Test
    fun validatesTwentyFourHourTimes() {
        assertTrue(DateUtils.isValidTime("09:30"))
        assertFalse(DateUtils.isValidTime("9:30 PM"))
    }
}
