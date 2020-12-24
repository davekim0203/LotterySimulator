package org.dave.lotterysimulatorwithstat.util

import org.dave.lotterysimulatorwithstat.database.LotteryType
import org.dave.lotterysimulatorwithstat.database.LotteryType.*


private fun generateRN(min: Int, max: Int) = (min..max).random()

private fun generateRNs(min: Int, max: Int, size: Int): List<Int> {
    val numbers = mutableListOf<Int>()
    while(numbers.size < size) {
        val randomNumber = generateRN(min, max)
        if(!numbers.contains(randomNumber)) numbers.add(randomNumber)
    }
    numbers.sort()

    return numbers
}

private fun generateBonusFromSamePool(min: Int, max: Int, winNums: List<Int>):Int {
    while (true) {
        val bonus = (min..max).random()
        if (!winNums.contains(bonus)) return bonus
    }
}

private fun generateTicketWithSamePoolBonus(min: Int, max: Int, size: Int): Pair<List<Int>, Int> {
    val winNums = generateRNs(min, max, size)
    return Pair(winNums, generateBonusFromSamePool(min, max, winNums))
}

fun generateTicketWithoutBonus(type: LotteryType): List<Int> {
    return when(type) {
        POWERBALL -> generateRNs(1, 69, 5)
        MEGA_MILLIONS -> generateRNs(1, 70, 5)

        //For Canadian Lotteries, these are used as ticket numbers
        LOTTO_MAX -> generateRNs(1, 50, 7)
        LOTTO_649 -> generateRNs(1, 49, 6)
    }
}

fun generateTicketWithBonus(type: LotteryType): Pair<List<Int>, Int> {
    return when(type) {
        //For US lotteries, these are used as both winning and ticket numbers
        POWERBALL -> Pair(generateTicketWithoutBonus(POWERBALL), generateRN(1, 26))
        MEGA_MILLIONS -> Pair(generateTicketWithoutBonus(MEGA_MILLIONS), generateRN(1, 25))

        //For Canadian lotteries, these are used as winning numbers only
        LOTTO_MAX -> generateTicketWithSamePoolBonus(1, 50, 7)
        LOTTO_649 -> generateTicketWithSamePoolBonus(1, 49, 6)
    }
}