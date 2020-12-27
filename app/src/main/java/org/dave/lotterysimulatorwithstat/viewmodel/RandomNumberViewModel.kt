package org.dave.lotterysimulatorwithstat.viewmodel

import android.content.Context
import android.text.SpannableString
import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.core.text.isDigitsOnly
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.dave.lotterysimulatorwithstat.adapter.PastWinningNumber
import org.dave.lotterysimulatorwithstat.database.LotteryType
import org.dave.lotterysimulatorwithstat.network.LotteryHistoryRepository
import org.dave.lotterysimulatorwithstat.network.NetworkResult
import org.dave.lotterysimulatorwithstat.network.PastMMWinningNumber
import org.dave.lotterysimulatorwithstat.network.PastPBWinningNumber
import org.dave.lotterysimulatorwithstat.util.*

class RandomNumberViewModel @ViewModelInject constructor(
    private val lotteryHistoryRepository: LotteryHistoryRepository
): ViewModel() {

    private val mTag = this::class.java.simpleName

    private val _isNetworkError = MutableLiveData<Boolean>()
    val isNetworkError: LiveData<Boolean>
        get() = _isNetworkError

    private val _showCopiedToClipboardToast = SingleLiveEvent<Any>()
    val showCopiedToClipboardToast: LiveData<Any>
        get() = _showCopiedToClipboardToast

    private val _generatedNumbers = MutableLiveData<String?>()
    val generatedNumbers: LiveData<String?>
        get() = _generatedNumbers

    private val _numberOfWins = MutableLiveData<Int>()
    val numberOfWins: LiveData<Int>
        get() = _numberOfWins

    private val _pastMatches = MutableLiveData<List<PastWinningNumber>>()
    val pastMatches: LiveData<List<PastWinningNumber>>
        get() = _pastMatches

    var isCheckboxChecked: Boolean = true
    private set

    private val _isPastListHidden = MutableLiveData<Boolean>()
    val isPastListHidden: LiveData<Boolean>
        get() = _isPastListHidden

    private val ticketType = MutableLiveData<LotteryType>()

    val pastMatchCheckboxEnabled = Transformations.map(ticketType) {
        it == LotteryType.POWERBALL || it == LotteryType.MEGA_MILLIONS
    }

    init {
        _isNetworkError.value = false
        _isPastListHidden.value = true
    }

    fun onGenerateButtonClicked(context: Context) {
        ticketType.value?.let {
            val newNumbers = generateTicketWithBonus(it)
            _generatedNumbers.value = convertNumbersToStringWithBonus(newNumbers)
            getTopMatches(context, newNumbers)
        }
    }

    fun onCheckedChanged(isChecked: Boolean) {
        isCheckboxChecked = isChecked
    }

    fun setLotteryType(type: LotteryType) {
        if(type != ticketType.value) {
            resetGeneratedNumbers()
        }
        ticketType.value = type
    }

    fun copyNumbersToClipboard(context: Context) {
        _generatedNumbers.value?.let {
            copyToClipboard(context, it)
            _showCopiedToClipboardToast.call()
        }
    }

    fun copyNumbersToClipboard(context: Context, text: String) {
        copyToClipboard(context, text)
        _showCopiedToClipboardToast.call()
    }

    private fun resetGeneratedNumbers() {
        _generatedNumbers.value = null
        hidePastList()
    }

    private fun getTopMatches(context: Context, newNumbers: Pair<List<Int>, Int>) {
        if(isCheckboxChecked) {
            when (ticketType.value) {
                LotteryType.POWERBALL -> getPowerballHistory(context, newNumbers)
                LotteryType.MEGA_MILLIONS -> getMegaMillionsHistory(context, newNumbers)
                LotteryType.LOTTO_MAX, LotteryType.LOTTO_649 -> {}
            }
        } else {
            hidePastList()
        }
    }

    private fun getPowerballHistory(context: Context, newNumbers: Pair<List<Int>, Int>) {
        EspressoIdlingResource.increment()
        viewModelScope.launch {
            when (val listResult = lotteryHistoryRepository.getPowerballHistory()) {
                is NetworkResult.Loading -> NetworkResult.Loading
                is NetworkResult.Success -> {
                    getPowerballTopMatches(context, newNumbers, listResult.data)
                    _isNetworkError.value = false
                    _isPastListHidden.value = false
                }
                is NetworkResult.Error -> {
                    Log.e(mTag, "Network error at Powerball history: ${listResult.exception}")
                    _isNetworkError.value = true
                    hidePastList()
                }
            }
            EspressoIdlingResource.decrement()
        }
    }

    private fun getPowerballTopMatches(context: Context, newNumbers: Pair<List<Int>, Int>, list: List<PastPBWinningNumber>) {
        val matchList = mutableListOf<PastWinningNumber>()
        for(i in list.indices) {
            val matchCount = getPowerballMatchCount(context, newNumbers, list[i].winningNumbers)
            if(matchCount.second.second || !(matchCount.second.first == 0 || matchCount.second.first == 1 || matchCount.second.first == 2)) {
                matchList.add(
                    PastWinningNumber(
                        list[i].drawDate.substring(0, 10),
                        HtmlCompat.toHtml(
                            matchCount.first,
                            HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL
                        ),
                        matchCount.second
                    )
                )
            }
        }

        val sortedListByMatch = matchList
            .sortedWith(compareBy ({ it.matchCount.first }, { it.matchCount.second }))
            .asReversed()
        _pastMatches.value = sortedListByMatch
        _numberOfWins.value = matchList.size
    }

    private fun getPowerballMatchCount(context: Context, newNumbers: Pair<List<Int>, Int>, winNums: String):
            Pair<SpannableString, Pair<Int, Boolean>> {
        val winNumsInt = toListOfInts(winNums)
        val winNumsWithoutBonus = winNumsInt.subList(0, winNumsInt.size - 1)
        val winNumsBonus = winNumsInt[winNumsInt.size - 1]

        return getResultUS(context, Pair(winNumsWithoutBonus, winNumsBonus), newNumbers)
    }

    private fun getMegaMillionsHistory(context: Context, newNumbers: Pair<List<Int>, Int>) {
        EspressoIdlingResource.increment()
        viewModelScope.launch {
            when (val listResult = lotteryHistoryRepository.getMegaMillionsHistory()) {
                is NetworkResult.Loading -> NetworkResult.Loading
                is NetworkResult.Success -> {
                    getMegaMillionsTopMatches(context, newNumbers, listResult.data)
                    _isNetworkError.value = false
                    _isPastListHidden.value = false
                }
                is NetworkResult.Error -> {
                    Log.e(mTag, "Network error at Mega Millions history: ${listResult.exception}")
                    _isNetworkError.value = true
                    hidePastList()
                }
            }
            EspressoIdlingResource.decrement()
        }
    }

    private fun getMegaMillionsTopMatches(context: Context, newNumbers: Pair<List<Int>, Int>, list: List<PastMMWinningNumber>) {
        val matchList = mutableListOf<PastWinningNumber>()
        for(i in list.indices) {
            val matchCount = getMegaMillionsMatchCount(context, newNumbers, list[i].winningNumbers, list[i].bonus)
            if(matchCount.second.second || !(matchCount.second.first == 0 || matchCount.second.first == 1 || matchCount.second.first == 2)) {
                matchList.add(
                    PastWinningNumber(
                        list[i].drawDate.substring(0, 10),
                        HtmlCompat.toHtml(
                            matchCount.first,
                            HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL
                        ),
                        matchCount.second
                    )
                )
            }
        }

        val sortedListByMatch = matchList
            .sortedWith(compareBy ({ it.matchCount.first }, { it.matchCount.second }))
            .asReversed()
        _pastMatches.value = sortedListByMatch
        _numberOfWins.value = matchList.size
    }

    private fun getMegaMillionsMatchCount(context: Context, newNumbers: Pair<List<Int>, Int>, winNums: String, bonus: Int):
            Pair<SpannableString, Pair<Int, Boolean>> {
        val winNumsInt = toListOfInts(winNums)
        return getResultUS(context, Pair(winNumsInt, bonus), newNumbers)
    }

    private fun toListOfInts(str: String): List<Int> {
        return str.trim()
            .split(" ")
            .filter { it.isNotEmpty() }
            .filter { it.isDigitsOnly() }
            .map { it.toInt() }
    }

    private fun hidePastList() {
        _isPastListHidden.value = true
        _pastMatches.value = listOf()
    }
}