package org.dave.lotterysimulatorwithstat.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import io.mockk.verify
import org.dave.lotterysimulatorwithstat.fragment.StatFragment.Companion.STAT_BAR_CHART_ID
import org.dave.lotterysimulatorwithstat.fragment.StatFragment.Companion.STAT_PIE_CHART_ID
import org.dave.lotterysimulatorwithstat.testUtil.getOrAwaitValue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@HiltAndroidTest
class StatViewModelTest {

    private val hiltRule = HiltAndroidRule(this)
    private val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: StatViewModel
    private val selectedStatTypeObserver = mockk<Observer<Int>>(relaxed = true)

    @get:Rule
    val rule: RuleChain = RuleChain
        .outerRule(hiltRule)
        .around(instantTaskExecutorRule)

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = StatViewModel()
    }

    @Test
    fun test_init() {
        viewModel.selectedStatType.observeForever(selectedStatTypeObserver)
        verify(exactly = 1) {
            selectedStatTypeObserver.onChanged(STAT_PIE_CHART_ID)
        }
        viewModel.selectedStatType.removeObserver(selectedStatTypeObserver)
    }

    @Test
    fun test_default_chart_type() {
        assertEquals(STAT_PIE_CHART_ID, viewModel.selectedStatType.getOrAwaitValue())
    }

    @Test
    fun test_toggle_chart_type() {
        viewModel.onBarChartButtonClick()
        assertEquals(STAT_BAR_CHART_ID, viewModel.selectedStatType.getOrAwaitValue())
        viewModel.onPieChartButtonClick()
        assertEquals(STAT_PIE_CHART_ID, viewModel.selectedStatType.getOrAwaitValue())
    }

    @Test
    fun test_toggle_isNoPayoutIncluded() {
        viewModel.setIsNoPayoutIncluded(true)
        assertEquals(true, viewModel.isNoPayoutIncluded.getOrAwaitValue())
        viewModel.setIsNoPayoutIncluded(false)
        assertEquals(false, viewModel.isNoPayoutIncluded.getOrAwaitValue())
    }
}