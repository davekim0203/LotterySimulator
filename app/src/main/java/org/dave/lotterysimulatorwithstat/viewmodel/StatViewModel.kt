package org.dave.lotterysimulatorwithstat.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.dave.lotterysimulatorwithstat.fragment.StatFragment.Companion.STAT_BAR_CHART_ID
import org.dave.lotterysimulatorwithstat.fragment.StatFragment.Companion.STAT_PIE_CHART_ID

class StatViewModel @ViewModelInject constructor(): ViewModel() {
    private val _selectedStatType = MutableLiveData<Int>()
    val selectedStatType: LiveData<Int>
        get() = _selectedStatType

    private val _isNoPayoutIncluded = MutableLiveData<Boolean>()
    val isNoPayoutIncluded: LiveData<Boolean>
        get() = _isNoPayoutIncluded

    init {
        select(STAT_PIE_CHART_ID)
    }

    fun onPieChartButtonClick() {
        select(STAT_PIE_CHART_ID)
    }

    fun onBarChartButtonClick() {
        select(STAT_BAR_CHART_ID)
    }

    fun setIsNoPayoutIncluded(isIncluded: Boolean) {
        _isNoPayoutIncluded.value = isIncluded
    }

    private fun select(type: Int) {
        if(_selectedStatType.value != type) {
            _selectedStatType.value = type
        }
    }
}