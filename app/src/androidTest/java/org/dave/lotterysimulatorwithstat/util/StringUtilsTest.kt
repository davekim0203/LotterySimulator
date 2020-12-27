package org.dave.lotterysimulatorwithstat.util

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.dave.lotterysimulatorwithstat.database.LotteryType
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StringUtilsTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun test_convertNumbersToStringWithBonus() {
        val ticketNumbersToTest = Pair(listOf(1, 3, 5, 11, 12, 23), 6)
        val actual = convertNumbersToStringWithBonus(ticketNumbersToTest)
        val expected = "01 03 05 11 12 23 + 06"
        assertEquals(expected, actual)
    }

    @Test
    fun test_getMatchCountString_withValidMatchCount() {
        //Powerball results
        assertEquals("5+", getMatchCountString(LotteryType.POWERBALL, 5, true))
        assertEquals("5", getMatchCountString(LotteryType.POWERBALL, 5, false))
        assertEquals("4+", getMatchCountString(LotteryType.POWERBALL, 4, true))
        assertEquals("4", getMatchCountString(LotteryType.POWERBALL, 4, false))
        assertEquals("3+", getMatchCountString(LotteryType.POWERBALL, 3, true))
        assertEquals("3", getMatchCountString(LotteryType.POWERBALL, 3, false))
        assertEquals("2+", getMatchCountString(LotteryType.POWERBALL, 2, true))
        assertEquals(":(", getMatchCountString(LotteryType.POWERBALL, 2, false))
        assertEquals("1+", getMatchCountString(LotteryType.POWERBALL, 1, true))
        assertEquals(":(", getMatchCountString(LotteryType.POWERBALL, 1, false))
        assertEquals("+", getMatchCountString(LotteryType.POWERBALL, 0, true))
        assertEquals(":(", getMatchCountString(LotteryType.POWERBALL, 0, false))

        //Mega Millions results
        assertEquals("5+", getMatchCountString(LotteryType.MEGA_MILLIONS, 5, true))
        assertEquals("5", getMatchCountString(LotteryType.MEGA_MILLIONS, 5, false))
        assertEquals("4+", getMatchCountString(LotteryType.MEGA_MILLIONS, 4, true))
        assertEquals("4", getMatchCountString(LotteryType.MEGA_MILLIONS, 4, false))
        assertEquals("3+", getMatchCountString(LotteryType.MEGA_MILLIONS, 3, true))
        assertEquals("3", getMatchCountString(LotteryType.MEGA_MILLIONS, 3, false))
        assertEquals("2+", getMatchCountString(LotteryType.MEGA_MILLIONS, 2, true))
        assertEquals(":(", getMatchCountString(LotteryType.MEGA_MILLIONS, 2, false))
        assertEquals("1+", getMatchCountString(LotteryType.MEGA_MILLIONS, 1, true))
        assertEquals(":(", getMatchCountString(LotteryType.MEGA_MILLIONS, 1, false))
        assertEquals("+", getMatchCountString(LotteryType.MEGA_MILLIONS, 0, true))
        assertEquals(":(", getMatchCountString(LotteryType.MEGA_MILLIONS, 0, false))

        //Lotto Max results
        assertEquals("7", getMatchCountString(LotteryType.LOTTO_MAX, 7, true))
        assertEquals("7", getMatchCountString(LotteryType.LOTTO_MAX, 7, false))
        assertEquals("6+", getMatchCountString(LotteryType.LOTTO_MAX, 6, true))
        assertEquals("6", getMatchCountString(LotteryType.LOTTO_MAX, 6, false))
        assertEquals("5+", getMatchCountString(LotteryType.LOTTO_MAX, 5, true))
        assertEquals("5", getMatchCountString(LotteryType.LOTTO_MAX, 5, false))
        assertEquals("4+", getMatchCountString(LotteryType.LOTTO_MAX, 4, true))
        assertEquals("4", getMatchCountString(LotteryType.LOTTO_MAX, 4, false))
        assertEquals("3+", getMatchCountString(LotteryType.LOTTO_MAX, 3, true))
        assertEquals("3", getMatchCountString(LotteryType.LOTTO_MAX, 3, false))
        assertEquals(":(", getMatchCountString(LotteryType.LOTTO_MAX, 2, true))
        assertEquals(":(", getMatchCountString(LotteryType.LOTTO_MAX, 2, false))
        assertEquals(":(", getMatchCountString(LotteryType.LOTTO_MAX, 1, true))
        assertEquals(":(", getMatchCountString(LotteryType.LOTTO_MAX, 1, false))
        assertEquals(":(", getMatchCountString(LotteryType.LOTTO_MAX, 0, true))
        assertEquals(":(", getMatchCountString(LotteryType.LOTTO_MAX, 0, false))

        //Lotto 649 results
        assertEquals("6", getMatchCountString(LotteryType.LOTTO_649, 6, true))
        assertEquals("6", getMatchCountString(LotteryType.LOTTO_649, 6, false))
        assertEquals("5+", getMatchCountString(LotteryType.LOTTO_649, 5, true))
        assertEquals("5", getMatchCountString(LotteryType.LOTTO_649, 5, false))
        assertEquals("4", getMatchCountString(LotteryType.LOTTO_649, 4, true))
        assertEquals("4", getMatchCountString(LotteryType.LOTTO_649, 4, false))
        assertEquals("3", getMatchCountString(LotteryType.LOTTO_649, 3, true))
        assertEquals("3", getMatchCountString(LotteryType.LOTTO_649, 3, false))
        assertEquals("2+", getMatchCountString(LotteryType.LOTTO_649, 2, true))
        assertEquals("2", getMatchCountString(LotteryType.LOTTO_649, 2, false))
        assertEquals(":(", getMatchCountString(LotteryType.LOTTO_649, 1, true))
        assertEquals(":(", getMatchCountString(LotteryType.LOTTO_649, 1, false))
        assertEquals(":(", getMatchCountString(LotteryType.LOTTO_649, 0, true))
        assertEquals(":(", getMatchCountString(LotteryType.LOTTO_649, 0, false))
    }

    @Test
    fun test_getMatchCountString_withInvalidMatchCount() {
        assertEquals("", getMatchCountString(LotteryType.POWERBALL, 9, true))
        assertEquals("", getMatchCountString(LotteryType.MEGA_MILLIONS, 9, true))
        assertEquals("", getMatchCountString(LotteryType.LOTTO_MAX, 9, true))
        assertEquals("", getMatchCountString(LotteryType.LOTTO_649, 9, true))
    }

    @Test
    fun test_getResultUS() {
        val winningNumbers = Pair(listOf(1, 2, 3, 4, 5, 6), 13)
        val ticketNumbers = Pair(listOf(1, 2, 31, 4, 51, 6), 13)
        val actual = getResultUS(context, ticketNumbers, winningNumbers)
        val expected = Pair(4, true)

        assertEquals(expected, actual.second)
    }

    @UiThreadTest
    @Test
    fun test_copyToClipboard() {
        val expected = "expected string"
        copyToClipboard(context, expected)

        val clipBoardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipBoardManager.addPrimaryClipChangedListener {
            val actual = clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()
            assertEquals(expected, actual)
        }
    }
}