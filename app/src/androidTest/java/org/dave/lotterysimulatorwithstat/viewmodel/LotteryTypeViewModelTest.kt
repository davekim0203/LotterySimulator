package org.dave.lotterysimulatorwithstat.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import io.mockk.verify
import org.dave.lotterysimulatorwithstat.database.LotteryType
import org.dave.lotterysimulatorwithstat.testUtil.getOrAwaitValue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@HiltAndroidTest
class LotteryTypeViewModelTest {

    private val hiltRule = HiltAndroidRule(this)
    private val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: LotteryTypeViewModel
    private val selectedLotteryTypeObserver = mockk<Observer<LotteryType>>(relaxed = true)

    @get:Rule
    val rule: RuleChain = RuleChain
        .outerRule(hiltRule)
        .around(instantTaskExecutorRule)

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = LotteryTypeViewModel()
    }

    @Test
    fun test_init() {
        viewModel.selected.observeForever(selectedLotteryTypeObserver)
        verify(exactly = 1) {
            selectedLotteryTypeObserver.onChanged(LotteryType.POWERBALL)
        }
        viewModel.selected.removeObserver(selectedLotteryTypeObserver)
    }

    @Test
    fun test_default_lottery_type() {
        assertEquals(LotteryType.POWERBALL, viewModel.selected.getOrAwaitValue())
    }

    @Test
    fun test_select_lottery_type() {
        viewModel.select(LotteryType.MEGA_MILLIONS.value)
        assertEquals(LotteryType.MEGA_MILLIONS, viewModel.selected.getOrAwaitValue())
    }
}