package org.dave.lotterysimulatorwithstat.database

import androidx.room.TypeConverter
import org.dave.lotterysimulatorwithstat.database.LotteryType.Companion.fromIntToLotteryType

class Converters {
    @TypeConverter
    fun fromLotteryTypeToInt(type: LotteryType): Int {
        return type.value
    }

    @TypeConverter
    fun convertToLotteryType(value: Int): LotteryType {
        return fromIntToLotteryType(value)!!
    }
}