package org.dave.lotterysimulatorwithstat.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ticket_table")
data class Ticket(
    @PrimaryKey(autoGenerate = true)
    var ticketId: Long = 0L,

    @ColumnInfo(name = "type")
    var type: LotteryType,

    @ColumnInfo(name = "numbers")
    var numbers: String,

    @ColumnInfo(name = "matchCount")
    var matchCount: String
)
