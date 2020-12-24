package org.dave.lotterysimulatorwithstat.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dave.lotterysimulatorwithstat.database.TicketRepository
import org.dave.lotterysimulatorwithstat.util.SingleLiveEvent

class TicketHistoryViewModel @ViewModelInject constructor(
    private val ticketRepository: TicketRepository
): ViewModel() {

    val tickets = ticketRepository.allTickets

    private val _showCopiedToClipboardToast = SingleLiveEvent<Any>()
    val showCopiedToClipboardToast: LiveData<Any>
        get() = _showCopiedToClipboardToast

    fun showCopiedToClipboardToast() {
        _showCopiedToClipboardToast.call()
    }

    fun clearAllTickets() = viewModelScope.launch(Dispatchers.IO) {
        ticketRepository.clearAllTickets()
    }
}