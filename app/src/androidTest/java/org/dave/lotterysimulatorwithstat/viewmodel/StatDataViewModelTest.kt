package org.dave.lotterysimulatorwithstat.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.dave.lotterysimulatorwithstat.database.LotteryType
import org.dave.lotterysimulatorwithstat.database.ResultCountRepository
import org.dave.lotterysimulatorwithstat.testUtil.getOrAwaitValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import javax.inject.Inject

@HiltAndroidTest
class StatDataViewModelTest {

    private val hiltRule = HiltAndroidRule(this)
    private val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: StatDataViewModel

    @get:Rule
    val rule: RuleChain = RuleChain
        .outerRule(hiltRule)
        .around(instantTaskExecutorRule)

    @Inject
    lateinit var resultCountRepository: ResultCountRepository

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = StatDataViewModel(resultCountRepository)
    }

    @Test
    fun test_setLotteryType() {
        viewModel.setIsNoPayoutIncluded(true)

        val expected1 = LotteryType.LOTTO_MAX
        viewModel.setLotteryType(expected1)
        val resultCounts1 = viewModel.resultCountsByType.getOrAwaitValue()
        assertEquals(expected1, resultCounts1[0].type)

        val expected2 = LotteryType.LOTTO_MAX
        viewModel.setLotteryType(expected2)
        val resultCounts2 = viewModel.resultCountsByType.getOrAwaitValue()
        assertEquals(expected2, resultCounts2[0].type)
    }

    @Test
    fun test_IsNoPayoutIncluded() {
        viewModel.setLotteryType(LotteryType.POWERBALL)

        viewModel.setIsNoPayoutIncluded(true)
        val resultCounts1 = viewModel.resultCountsByType.getOrAwaitValue()
        assertEquals(":(", resultCounts1[0].matchCount)

        viewModel.setIsNoPayoutIncluded(false)
        val resultCounts2 = viewModel.resultCountsByType.getOrAwaitValue()
        for(rc in resultCounts2) {
            assertNotEquals(":(", rc.matchCount)
        }
    }

    @Test
    fun test_totalCount() {
        viewModel.setLotteryType(LotteryType.POWERBALL)
        viewModel.setIsNoPayoutIncluded(true)
        val resultCounts1 = viewModel.resultCountsByType.getOrAwaitValue()
        var expectedCount = 0
        for(rc in resultCounts1) {
            expectedCount += rc.count
        }
        val actual = viewModel.totalCount.getOrAwaitValue()
        assertEquals(expectedCount, actual)
    }
}