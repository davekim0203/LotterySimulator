package org.dave.lotterysimulatorwithstat.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ResultCountDao {

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    fun insert(resultCount: ResultCount)

    @Insert
    fun insertAll(resultCounts: List<ResultCount>)

    @Query("SELECT * FROM result_count_table WHERE type = :type ORDER BY CASE WHEN matchCount = ':(' THEN 0 ELSE 1 END, matchCount")
    fun getResultsCountsByType(type: LotteryType): LiveData<List<ResultCount>>

    @Query("UPDATE result_count_table SET count = count + :addCount WHERE type = :type AND matchCount = :matchCount")
    fun updateResultCount(type: LotteryType, matchCount: String, addCount: Int)

    @Query("UPDATE result_count_table SET count = 0 WHERE type = :type")
    fun resetStatByType(type: LotteryType)

    @Query("UPDATE result_count_table SET count = 0")
    fun resetAllStat()
}