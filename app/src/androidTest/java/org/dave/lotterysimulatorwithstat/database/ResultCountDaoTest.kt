package org.dave.lotterysimulatorwithstat.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.dave.lotterysimulatorwithstat.testUtil.getOrAwaitValue
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultCountDaoTest {

    private lateinit var resultCountDatabase: ResultCountDatabase
    private lateinit var resultCountDao: ResultCountDao
    private val resultCount1 = ResultCount(LotteryType.POWERBALL, "3+", 0)
    private val resultCount2 = ResultCount(LotteryType.POWERBALL, "2+", 3)
    private val resultCount3 = ResultCount(LotteryType.POWERBALL, "+", 13)
    private val resultCount4 = ResultCount(LotteryType.POWERBALL, ":(", 23)
    private val resultCount5 = ResultCount(LotteryType.LOTTO_MAX, "3", 7)
    private val resultCount6 = ResultCount(LotteryType.LOTTO_MAX, ":(", 17)
    private val testData =
        listOf(resultCount1, resultCount2, resultCount3, resultCount4, resultCount5, resultCount6)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupDatabase() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        resultCountDatabase =
            Room.inMemoryDatabaseBuilder(context, ResultCountDatabase::class.java).build()
        resultCountDao = resultCountDatabase.resultCountDao()

        resultCountDao.insertAll(testData)
    }

    @After
    fun closeDatabase() {
        resultCountDatabase.close()
    }

    @Test
    fun test_getResultsCountsByType() {
        val resultCountsPb =
            resultCountDao.getResultsCountsByType(LotteryType.POWERBALL).getOrAwaitValue()
        var expected =
            listOf(resultCount4, resultCount3, resultCount2, resultCount1)       //right order
        assertEquals(expected, resultCountsPb)

        val resultCountsMax =
            resultCountDao.getResultsCountsByType(LotteryType.LOTTO_MAX).getOrAwaitValue()
        expected = listOf(resultCount6, resultCount5)       //right order
        assertEquals(expected, resultCountsMax)
    }

    @Test
    fun test_updateResultCount() {
        resultCountDao.updateResultCount(LotteryType.POWERBALL, ":(", 7)
        val updatedResultCount =
            resultCountDao.getResultsCountsByType(LotteryType.POWERBALL).getOrAwaitValue()[0]
        val updatedCount = updatedResultCount.count
        val expected = 30
        assertEquals(expected, updatedCount)
    }

    @Test
    fun test_resetStatByType() {
        resultCountDao.resetStatByType(LotteryType.POWERBALL)
        val resetResultCountsPb =
            resultCountDao.getResultsCountsByType(LotteryType.POWERBALL).getOrAwaitValue()
        assertEquals(0, resetResultCountsPb[0].count)
        assertEquals(0, resetResultCountsPb[1].count)
        assertEquals(0, resetResultCountsPb[2].count)
        assertEquals(0, resetResultCountsPb[3].count)

        val resultCountsMax = resultCountDao.getResultsCountsByType(LotteryType.LOTTO_MAX).getOrAwaitValue()
        assertEquals(17, resultCountsMax[0].count)
        assertEquals(7, resultCountsMax[1].count)

        resultCountDao.resetStatByType(LotteryType.LOTTO_MAX)
        val resetResultCountsMax =
            resultCountDao.getResultsCountsByType(LotteryType.LOTTO_MAX).getOrAwaitValue()
        assertEquals(0, resetResultCountsMax[0].count)
        assertEquals(0, resetResultCountsMax[1].count)
    }

    @Test
    fun test_resetAllStat() {
        resultCountDao.resetAllStat()
        val resetResultCountsPb =
            resultCountDao.getResultsCountsByType(LotteryType.POWERBALL).getOrAwaitValue()
        val resetResultCountsMax =
            resultCountDao.getResultsCountsByType(LotteryType.LOTTO_MAX).getOrAwaitValue()
        assertEquals(0, resetResultCountsPb[0].count)
        assertEquals(0, resetResultCountsPb[1].count)
        assertEquals(0, resetResultCountsPb[2].count)
        assertEquals(0, resetResultCountsPb[3].count)
        assertEquals(0, resetResultCountsMax[0].count)
        assertEquals(0, resetResultCountsMax[1].count)
    }
}