package org.dave.lotterysimulatorwithstat.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.annotation.UiThreadTest
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.*
import org.dave.lotterysimulatorwithstat.database.*
import org.dave.lotterysimulatorwithstat.fragment.SimulationFragment.Companion.NO_NUMBER_WARNING_DIALOG_ID
import org.dave.lotterysimulatorwithstat.fragment.SimulationFragment.Companion.ZERO_NUMBER_WARNING_DIALOG_ID
import org.dave.lotterysimulatorwithstat.testUtil.getOrAwaitValue
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.rules.RuleChain
import javax.inject.Inject

@HiltAndroidTest
class SimulationViewModelTest {

    private val hiltRule = HiltAndroidRule(this)
    private val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SimulationViewModel
    private lateinit var context: Context
    private val showCopiedToClipboardToastObserver = mockk<Observer<Any>>(relaxed = true)
    private val newTicketsObserver = mockk<Observer<List<Ticket>>>(relaxed = true)
    private val winningNumbersObserver = mockk<Observer<Pair<List<Int>, Int>>>(relaxed = true)
    private val resultsObserver = mockk<Observer<String>>(relaxed = true)
    private val showResultsObserver = mockk<Observer<Boolean>>(relaxed = true)
    private val dialogIdObserver = mockk<Observer<Int>>(relaxed = true)

    @get:Rule
    val rule: RuleChain = RuleChain
        .outerRule(hiltRule)
        .around(instantTaskExecutorRule)

    @Inject
    lateinit var ticketRepository: TicketRepository

    @Inject
    lateinit var resultCountRepository: ResultCountRepository

    @Before
    fun setup() {
        hiltRule.inject()

        context = InstrumentationRegistry.getInstrumentation().targetContext
        viewModel = SimulationViewModel(ticketRepository, resultCountRepository)
    }

    @Test
    fun test_init() {
        viewModel.showResults.observeForever(showResultsObserver)

        verify(exactly = 1) {
            showResultsObserver.onChanged(false)
        }

        viewModel.showResults.removeObserver(showResultsObserver)
    }

    @Test
    fun test_toggleShowResults() {
        assertEquals(false, viewModel.showResults.getOrAwaitValue())
        viewModel.toggleShowResults()
        assertEquals(true, viewModel.showResults.getOrAwaitValue())
        viewModel.toggleShowResults()
        assertEquals(false, viewModel.showResults.getOrAwaitValue())
    }

    @Test
    fun test_results_onSimulateButtonClicked() {
        viewModel.results.observeForever(resultsObserver)

        viewModel.onSimulateButtonClicked(context)
        verify(exactly = 1) {
            resultsObserver.onChanged(any())
        }

        viewModel.results.removeObserver(resultsObserver)
    }

    @Test
    fun test_dialogId_onSimulateButtonClicked() {
        viewModel.dialogId.observeForever(dialogIdObserver)

        viewModel.numOfTickets = -1
        viewModel.onSimulateButtonClicked(context)
        verify(exactly = 1) {
            dialogIdObserver.onChanged(NO_NUMBER_WARNING_DIALOG_ID)
        }

        viewModel.numOfTickets = 0
        viewModel.onSimulateButtonClicked(context)
        verify(exactly = 1) {
            dialogIdObserver.onChanged(ZERO_NUMBER_WARNING_DIALOG_ID)
        }

        viewModel.dialogId.removeObserver(dialogIdObserver)
    }

    @Test
    fun test_numOfTickets_onSimulateButtonClicked() {
        val expected = 3
        viewModel.numOfTickets = expected
        viewModel.onSimulateButtonClicked(context)
        assertEquals(expected, viewModel.newTickets.getOrAwaitValue().size)
    }

    @Test
    fun test_newTickets_onSimulateButtonClicked() {
        viewModel.newTickets.observeForever(newTicketsObserver)
        viewModel.onSimulateButtonClicked(context)

        verify(exactly = 1) {
            newTicketsObserver.onChanged(any())
        }

        viewModel.newTickets.removeObserver(newTicketsObserver)
    }

    @Test
    fun test_winningNumbers_onSimulateButtonClicked() {
        viewModel.winningNumbers.observeForever(winningNumbersObserver)

        viewModel.onSimulateButtonClicked(context)
        verify(exactly = 1){
            winningNumbersObserver.onChanged(any())
        }

        viewModel.onCheckedChanged(false)
        viewModel.onSimulateButtonClicked(context)
        viewModel.onSimulateButtonClicked(context)
        verify(exactly = 3) {
            winningNumbersObserver.onChanged(any())
        }

        viewModel.onCheckedChanged(true)
        viewModel.onSimulateButtonClicked(context)
        verify(exactly = 3) {
            winningNumbersObserver.onChanged(any())
        }

        viewModel.winningNumbers.removeObserver(winningNumbersObserver)
    }

    @Test
    fun test_winningNumbers_setTicketType() {
        viewModel.winningNumbers.observeForever(winningNumbersObserver)

        viewModel.setTicketType(LotteryType.MEGA_MILLIONS)
        verify(exactly = 1){
            winningNumbersObserver.onChanged(null)
        }

        //If the same lottery type is set again, onChanged not triggered
        viewModel.setTicketType(LotteryType.MEGA_MILLIONS)
        verify(exactly = 1){
            winningNumbersObserver.onChanged(null)
        }

        viewModel.winningNumbers.removeObserver(winningNumbersObserver)
    }

    @UiThreadTest
    @Test
    fun test_copyNumbersToClipboard() {
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
        viewModel.numOfTickets = 3
        viewModel.onSimulateButtonClicked(context)
        viewModel.copyNumbersToClipboard(context)

        verify(exactly = 1) {
            showCopiedToClipboardToastObserver.onChanged(null)
        }

        viewModel.showCopiedToClipboardToast.removeObserver(showCopiedToClipboardToastObserver)
    }

    @Test
    fun test_onNumberOfTicketsTextChanged() {
        val testCharSequence1: CharSequence = ""
        viewModel.onNumberOfTicketsTextChanged(testCharSequence1)
        assertEquals(-1, viewModel.numOfTickets)

        val testCharSequence2: CharSequence = "5"
        viewModel.onNumberOfTicketsTextChanged(testCharSequence2)
        assertEquals(5, viewModel.numOfTickets)
    }
}