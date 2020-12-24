package org.dave.lotterysimulatorwithstat.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.annotation.UiThreadTest
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.*
import org.dave.lotterysimulatorwithstat.adapter.PastWinningNumber
import org.dave.lotterysimulatorwithstat.database.*
import org.dave.lotterysimulatorwithstat.network.LotteryHistoryRepository
import org.dave.lotterysimulatorwithstat.testUtil.getOrAwaitValue
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.rules.RuleChain
import javax.inject.Inject

@HiltAndroidTest
class RandomNumberViewModelTest {

    private val hiltRule = HiltAndroidRule(this)
    private val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RandomNumberViewModel
    private lateinit var context: Context
    private val generatedNumbersObserver = mockk<Observer<String?>>(relaxed = true)
    private val isPastListHiddenObserver = mockk<Observer<Boolean>>(relaxed = true)
    private val pastMatchesObserver = mockk<Observer<List<PastWinningNumber>>>(relaxed = true)
    private val isNetworkErrorObserver = mockk<Observer<Boolean>>(relaxed = true)
    private val showCopiedToClipboardToastObserver = mockk<Observer<Any>>(relaxed = true)

    @get:Rule
    val rule: RuleChain = RuleChain
        .outerRule(hiltRule)
        .around(instantTaskExecutorRule)

    @Inject
    lateinit var lotteryHistoryRepository: LotteryHistoryRepository

    @Before
    fun setup() {
        hiltRule.inject()

        context = InstrumentationRegistry.getInstrumentation().targetContext
        viewModel = RandomNumberViewModel(lotteryHistoryRepository)
    }

    @Test
    fun test_init() {
        viewModel.isNetworkError.observeForever(isNetworkErrorObserver)
        viewModel.isPastListHidden.observeForever(isPastListHiddenObserver)

        verify(exactly = 1) {
            isNetworkErrorObserver.onChanged(false)
            isPastListHiddenObserver.onChanged(true)
        }

        viewModel.isNetworkError.removeObserver(isNetworkErrorObserver)
        viewModel.isPastListHidden.removeObserver(isPastListHiddenObserver)
    }

    @Test
    fun test_setLotteryType() {
        viewModel.generatedNumbers.observeForever(generatedNumbersObserver)
        viewModel.isPastListHidden.observeForever(isPastListHiddenObserver)
        viewModel.pastMatches.observeForever(pastMatchesObserver)

        viewModel.setLotteryType(LotteryType.POWERBALL)
        verify(exactly = 1) {
            generatedNumbersObserver.onChanged(null)
            pastMatchesObserver.onChanged(listOf())
        }
        /**
         * isPastListHidden is set to true twice at this point because
         * it's set to true in init{}
         */
        verify(exactly = 2) {
            isPastListHiddenObserver.onChanged(true)
        }

        //if same lottery type, don't update
        viewModel.setLotteryType(LotteryType.POWERBALL)
        verify(exactly = 1) {
            generatedNumbersObserver.onChanged(null)
            pastMatchesObserver.onChanged(listOf())
        }
        verify(exactly = 2) {
            isPastListHiddenObserver.onChanged(true)
        }

        viewModel.setLotteryType(LotteryType.LOTTO_649)
        verify(exactly = 2) {
            generatedNumbersObserver.onChanged(null)
            pastMatchesObserver.onChanged(listOf())
        }
        verify(exactly = 3) {
            isPastListHiddenObserver.onChanged(true)
        }

        viewModel.generatedNumbers.removeObserver(generatedNumbersObserver)
        viewModel.isPastListHidden.removeObserver(isPastListHiddenObserver)
        viewModel.pastMatches.removeObserver(pastMatchesObserver)
    }

    @Test
    fun test_onGenerateButtonClicked_when_isCheckboxChecked_false() {
        viewModel.generatedNumbers.observeForever(generatedNumbersObserver)
        viewModel.isPastListHidden.observeForever(isPastListHiddenObserver)
        viewModel.pastMatches.observeForever(pastMatchesObserver)

        viewModel.setLotteryType(LotteryType.POWERBALL)
        viewModel.onCheckedChanged(false)
        viewModel.onGenerateButtonClicked(context)
        verify(exactly = 2) {
            /**
             * 1. setLotteryType()
             * 2. onGenerateButtonClicked()
             */
            generatedNumbersObserver.onChanged(any())
            pastMatchesObserver.onChanged(listOf())
        }
        verify(exactly = 3) {
            /**
             * 1. init{}
             * 2. setLotteryType()
             * 3. onGenerateButtonClicked()
             */
            isPastListHiddenObserver.onChanged(true)
        }
        assertEquals(listOf<PastWinningNumber>(), viewModel.pastMatches.getOrAwaitValue())
        assertEquals(true, viewModel.isPastListHidden.getOrAwaitValue())

        viewModel.generatedNumbers.removeObserver(generatedNumbersObserver)
        viewModel.isPastListHidden.removeObserver(isPastListHiddenObserver)
        viewModel.pastMatches.removeObserver(pastMatchesObserver)
    }

    @Test
    fun test_pastMatchCheckboxEnabled() {
        viewModel.setLotteryType(LotteryType.POWERBALL)
        assertEquals(true, viewModel.pastMatchCheckboxEnabled.getOrAwaitValue())
        viewModel.setLotteryType(LotteryType.MEGA_MILLIONS)
        assertEquals(true, viewModel.pastMatchCheckboxEnabled.getOrAwaitValue())
        viewModel.setLotteryType(LotteryType.LOTTO_MAX)
        assertEquals(false, viewModel.pastMatchCheckboxEnabled.getOrAwaitValue())
        viewModel.setLotteryType(LotteryType.LOTTO_649)
        assertEquals(false, viewModel.pastMatchCheckboxEnabled.getOrAwaitValue())
    }

    @Test
    fun test_onCheckedChanged() {
        viewModel.onCheckedChanged(true)
        assertEquals(true, viewModel.isCheckboxChecked)
        viewModel.onCheckedChanged(false)
        assertEquals(false, viewModel.isCheckboxChecked)
    }

    @UiThreadTest
    @Test
    fun test_copyNumbersToClipboard_with_input() {
        viewModel.showCopiedToClipboardToast.observeForever(showCopiedToClipboardToastObserver)
        viewModel.copyNumbersToClipboard(context, "test text")

        verify(exactly = 1) {
            showCopiedToClipboardToastObserver.onChanged(null)
        }

        viewModel.showCopiedToClipboardToast.removeObserver(showCopiedToClipboardToastObserver)
    }

    @UiThreadTest
    @Test
    fun test_copyNumbersToClipboard_winningNumbers() {
        viewModel.showCopiedToClipboardToast.observeForever(showCopiedToClipboardToastObserver)
        viewModel.setLotteryType(LotteryType.POWERBALL)
        viewModel.onGenerateButtonClicked(context)
        viewModel.copyNumbersToClipboard(context)

        verify(exactly = 1) {
            showCopiedToClipboardToastObserver.onChanged(null)
        }

        viewModel.showCopiedToClipboardToast.removeObserver(showCopiedToClipboardToastObserver)
    }
}