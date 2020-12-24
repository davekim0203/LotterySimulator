package org.dave.lotterysimulatorwithstat.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.annotation.UiThreadTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import io.mockk.verify
import org.dave.lotterysimulatorwithstat.database.TicketRepository
import org.dave.lotterysimulatorwithstat.testUtil.getOrAwaitValue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import javax.inject.Inject

@HiltAndroidTest
class TicketHistoryViewModelTest {

    private val hiltRule = HiltAndroidRule(this)
    private val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TicketHistoryViewModel
    private val showCopiedToClipboardToastObserver = mockk<Observer<Any>>(relaxed = true)

    @get:Rule
    val rule: RuleChain = RuleChain
        .outerRule(hiltRule)
        .around(instantTaskExecutorRule)

    @Inject
    lateinit var ticketRepository: TicketRepository

    @Before
    fun setup() {
        hiltRule.inject()

        viewModel = TicketHistoryViewModel(ticketRepository)
    }

    @Test
    fun test_tickets() {
        val expected = ticketRepository.allTickets.getOrAwaitValue()
        val actual = viewModel.tickets.getOrAwaitValue()
        assertEquals(expected, actual)
    }

    @UiThreadTest
    @Test
    fun test_copyNumbersToClipboard() {
        viewModel.showCopiedToClipboardToast.observeForever(showCopiedToClipboardToastObserver)
        viewModel.showCopiedToClipboardToast()

        verify(exactly = 1) {
            showCopiedToClipboardToastObserver.onChanged(null)
        }

        viewModel.showCopiedToClipboardToast.removeObserver(showCopiedToClipboardToastObserver)
    }
}