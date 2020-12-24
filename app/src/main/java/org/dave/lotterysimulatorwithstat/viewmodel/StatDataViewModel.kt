package org.dave.lotterysimulatorwithstat.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dave.lotterysimulatorwithstat.database.LotteryType
import org.dave.lotterysimulatorwithstat.database.ResultCountRepository

class StatDataViewModel @ViewModelInject constructor(
    private val resultCountRepository: ResultCountRepository
): ViewModel() {

    private val ticketType = MutableLiveData<LotteryType>()
    private val isNoPayoutIncluded = MutableLiveData<Boolean>()
    private val combinedLiveData = MediatorLiveData<Pair<LotteryType?, Boolean?>>().apply {
        addSource(ticketType) {
            value = Pair(it, isNoPayoutIncluded.value)
        }
        addSource(isNoPayoutIncluded) {
            value = Pair(ticketType.value, it)
        }
    }

    val resultCountsByType = Transformations.switchMap(combinedLiveData) {
        val type = it.first
        val isIncluded = it.second
        if(type != null && isIncluded != null) {
            val allResultCounts = resultCountRepository.getResultsCountByType(type)
            if(isIncluded) {
                allResultCounts
            } else {
                Transformations.map(allResultCounts){ list ->
                    list.subList(1, list.size)
                }
            }
        } else {
            null
        }
    }

    val totalCount = Transformations.map(resultCountsByType) {
        var total = 0
        for(c in it) {
            total += c.count
        }
        total
    }

    fun setLotteryType(type: LotteryType) {
        ticketType.value = type
    }

    fun setIsNoPayoutIncluded(isIncluded: Boolean) {
        isNoPayoutIncluded.value = isIncluded
    }

    fun resetStatByType(type: LotteryType) = viewModelScope.launch(Dispatchers.IO) {
        resultCountRepository.resetStatByType(type)
    }

    fun resetAllStat() = viewModelScope.launch(Dispatchers.IO) {
        resultCountRepository.resetAllStat()
    }
}