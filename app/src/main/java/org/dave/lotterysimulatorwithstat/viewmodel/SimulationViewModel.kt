package org.dave.lotterysimulatorwithstat.viewmodel

import android.content.Context
import androidx.core.text.HtmlCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.database.LotteryType
import org.dave.lotterysimulatorwithstat.database.LotteryType.POWERBALL
import org.dave.lotterysimulatorwithstat.database.ResultCountRepository
import org.dave.lotterysimulatorwithstat.database.Ticket
import org.dave.lotterysimulatorwithstat.database.TicketRepository
import org.dave.lotterysimulatorwithstat.fragment.SimulationFragment.Companion.NO_NUMBER_WARNING_DIALOG_ID
import org.dave.lotterysimulatorwithstat.fragment.SimulationFragment.Companion.ZERO_NUMBER_WARNING_DIALOG_ID
import org.dave.lotterysimulatorwithstat.util.*

class SimulationViewModel @ViewModelInject constructor(
    private val ticketRepository: TicketRepository,
    private val resultCountRepository: ResultCountRepository
): ViewModel() {

    private var ticketType = POWERBALL
    var isWinningNumbersFixed = true
    private set

    private val _winningNumbers = MutableLiveData<Pair<List<Int>, Int>>()
    val winningNumbers: LiveData<Pair<List<Int>, Int>>
        get() = _winningNumbers

    private val _newTickets = MutableLiveData<List<Ticket>>()
    val newTickets: LiveData<List<Ticket>>
        get() = _newTickets

    private val _results = MutableLiveData<String>()
    val results: LiveData<String>
        get() = _results

    private val _showResults = MutableLiveData<Boolean>()
    val showResults: LiveData<Boolean>
        get() = _showResults

    private val _dialogId = SingleLiveEvent<Int>()
    val dialogId: LiveData<Int>
        get() = _dialogId

    private val _showCopiedToClipboardToast = SingleLiveEvent<Any>()
    val showCopiedToClipboardToast: LiveData<Any>
        get() = _showCopiedToClipboardToast

    var numOfTickets: Int = DEFAULT_NUM_OF_TICKETS
    val fixCheckboxEnabled = Transformations.map(winningNumbers) { it != null }

    init {
        _showResults.value = false
    }

    fun onNumberOfTicketsTextChanged(text: CharSequence) {
        val inputString = text.toString()
        numOfTickets = if(inputString == "") {
            -1
        } else {
            inputString.toInt()
        }
    }

    fun onSimulateButtonClicked(context: Context) {
        when (numOfTickets) {
            -1 -> _dialogId.value = NO_NUMBER_WARNING_DIALOG_ID
            0 -> _dialogId.value = ZERO_NUMBER_WARNING_DIALOG_ID
            else -> simulateTickets(context)
        }
    }

    private fun simulateTickets(context: Context) {
        if(!isWinningNumbersFixed || _winningNumbers.value == null) {
            _winningNumbers.value = generateTicketWithBonus(ticketType)
        }

        val newTickets = mutableListOf<Ticket>()
        val resultCountMap = mutableMapOf<String, Int>()
        for(i in 1..numOfTickets) {
            _winningNumbers.value?.let {
                val result = getNewTicketWithResult(context, ticketType, it)
                newTickets.add(
                    Ticket(
                        type = ticketType,
                        numbers = HtmlCompat.toHtml(
                            result.first,
                            HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL
                        ),
                        matchCount = result.second
                    )
                )

                //Count results from new tickets
                if(result.second in resultCountMap.keys) {
                    val count = resultCountMap[result.second] ?: 0
                    resultCountMap[result.second] = count + 1
                } else {
                    resultCountMap[result.second] = 1
                }
            }
        }
        _newTickets.value = newTickets
        insertTickets(newTickets)

        //Update result counts in db
        for(key in resultCountMap.keys){
            val count = resultCountMap[key] ?: 0
            updateResultCount(ticketType, key, count)
        }

        _results.value = formatResultsString(context, resultCountMap)
        deleteOldTickets()
    }

    fun toggleShowResults() {
        _showResults.value?.let {
            _showResults.value = !it
        }
    }

    fun onCheckedChanged(checked: Boolean) {
        isWinningNumbersFixed = checked
    }

    fun setTicketType(type: LotteryType) {
        if(type != ticketType) {
            resetWinningNumbers()
            ticketType = type
        }
    }

    fun copyNumbersToClipboard(context: Context) {
        _winningNumbers.value?.let {
            copyToClipboard(context, convertNumbersToStringWithBonus(it))
            _showCopiedToClipboardToast.call()
        }
    }

    fun copyNumbersToClipboard(context: Context, text: String) {
        copyToClipboard(context, text)
        _showCopiedToClipboardToast.call()
    }

    private fun resetWinningNumbers() {
        _winningNumbers.value = null
    }

    private fun formatResultsString(context: Context, results: Map<String, Int>): String {
        val sortedResults = results.toSortedMap(compareBy { it })

        var resultsString = ""
        val noPayoutString = context.getString(R.string.match_count_else)
        val noPayoutExists = sortedResults.contains(noPayoutString)
        var isFirst = !noPayoutExists

        if(noPayoutExists) {
            resultsString = "$noPayoutString: ${sortedResults[noPayoutString]}"
            sortedResults.remove(noPayoutString)
        }

        for(result in sortedResults.keys) {
            if(isFirst) {
                resultsString = "$result: ${results[result]}"
                isFirst = false
            } else {
                resultsString = "$resultsString     $result: ${results[result]}"
            }
        }

        return resultsString
    }

    private fun updateResultCount(type: LotteryType, matchCount: String, addCount: Int) = viewModelScope.launch(Dispatchers.IO) {
        resultCountRepository.updateResultCount(type, matchCount, addCount)
    }

    private fun insertTickets(tickets: List<Ticket>) = viewModelScope.launch(Dispatchers.IO) {
        ticketRepository.insertAll(tickets)
    }

    private fun deleteOldTickets() = viewModelScope.launch(Dispatchers.IO) {
        ticketRepository.deleteOldTickets()
    }

    companion object {
        private const val DEFAULT_NUM_OF_TICKETS = 10
    }
}