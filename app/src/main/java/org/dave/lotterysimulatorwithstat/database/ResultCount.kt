package org.dave.lotterysimulatorwithstat.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "result_count_table", primaryKeys = ["type", "matchCount"])
data class ResultCount(
    @ColumnInfo(name = "type")
    var type: LotteryType,

    @ColumnInfo(name = "matchCount")
    var matchCount: String,

    @ColumnInfo(name = "count")
    var count: Int
)