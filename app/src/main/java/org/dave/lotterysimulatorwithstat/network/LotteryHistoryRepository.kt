package org.dave.lotterysimulatorwithstat.network

import javax.inject.Inject

class LotteryHistoryRepository @Inject constructor(private val lotteryHistoryService: LotteryHistoryService) {

    suspend fun getPowerballHistory(): NetworkResult<List<PastPBWinningNumber>> {
        return try {
            NetworkResult.Success(lotteryHistoryService.getPowerballHistory())
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    suspend fun getMegaMillionsHistory(): NetworkResult<List<PastMMWinningNumber>> {
        return try {
            NetworkResult.Success(lotteryHistoryService.getMegaMillionsHistory())
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
}

sealed class NetworkResult<out R> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Exception) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}