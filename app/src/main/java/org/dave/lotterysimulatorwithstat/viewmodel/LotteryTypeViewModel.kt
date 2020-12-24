package org.dave.lotterysimulatorwithstat.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.dave.lotterysimulatorwithstat.database.LotteryType
import org.dave.lotterysimulatorwithstat.database.LotteryType.Companion.fromIntToLotteryType

class LotteryTypeViewModel @ViewModelInject constructor(): ViewModel() {
    private val _selected = MutableLiveData<LotteryType>()
    val selected: LiveData<LotteryType>
        get() = _selected

    fun select(selectedType: Int) {
        _selected.value = fromIntToLotteryType(selectedType)
    }

    init {
        //initial value
        _selected.value = LotteryType.POWERBALL
    }
}