package org.dave.lotterysimulatorwithstat.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResultCountRepository @Inject constructor(private val resultCountDao: ResultCountDao) {

    fun getResultsCountByType(type: LotteryType): LiveData<List<ResultCount>> {
        return resultCountDao.getResultsCountsByType(type)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateResultCount(type: LotteryType, matchCount: String, addCount: Int) {
        resultCountDao.updateResultCount(type, matchCount, addCount)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun resetStatByType(type: LotteryType) {
        resultCountDao.resetStatByType(type)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun resetAllStat() {
        resultCountDao.resetAllStat()
    }
}
