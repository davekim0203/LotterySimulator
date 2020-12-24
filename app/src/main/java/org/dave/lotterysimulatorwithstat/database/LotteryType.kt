package org.dave.lotterysimulatorwithstat.database

import androidx.annotation.StringRes
import org.dave.lotterysimulatorwithstat.R

enum class LotteryType(val value: Int, @StringRes val nameId: Int, @StringRes val shortNameId: Int) {
    POWERBALL(0, R.string.lottery_name_powerball, R.string.lottery_short_name_powerball),
    MEGA_MILLIONS(1, R.string.lottery_name_mega_millions, R.string.lottery_short_name_mega_millions),
    LOTTO_MAX(2, R.string.lottery_name_lotto_max, R.string.lottery_short_name_lotto_max),
    LOTTO_649(3, R.string.lottery_name_lotto_649, R.string.lottery_short_name_lotto_649);

    companion object {
        private val map = values().associateBy(LotteryType::value)
        fun fromIntToLotteryType(type: Int): LotteryType? {
            require(type in 0..3)
            return map[type]
        }
    }
}