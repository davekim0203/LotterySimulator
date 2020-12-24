package org.dave.lotterysimulatorwithstat.database

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class ConvertersTest {

    private lateinit var converters : Converters

    @Before
    fun setup() {
        converters = Converters()
    }

    @Test
    fun test_fromLotteryTypeToInt() {
        assertEquals(0, converters.fromLotteryTypeToInt(LotteryType.POWERBALL))
        assertEquals(1, converters.fromLotteryTypeToInt(LotteryType.MEGA_MILLIONS))
        assertEquals(2, converters.fromLotteryTypeToInt(LotteryType.LOTTO_MAX))
        assertEquals(3, converters.fromLotteryTypeToInt(LotteryType.LOTTO_649))
    }

    @Test
    fun test_convertToLotteryType() {
        assertEquals(LotteryType.POWERBALL, converters.convertToLotteryType(0))
        assertEquals(LotteryType.MEGA_MILLIONS, converters.convertToLotteryType(1))
        assertEquals(LotteryType.LOTTO_MAX, converters.convertToLotteryType(2))
        assertEquals(LotteryType.LOTTO_649, converters.convertToLotteryType(3))
    }
}