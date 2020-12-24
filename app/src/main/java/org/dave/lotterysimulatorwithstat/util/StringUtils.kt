package org.dave.lotterysimulatorwithstat.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat.getColor
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.database.LotteryType
import org.dave.lotterysimulatorwithstat.database.LotteryType.*


private fun convertNumbersToStringWithoutBonus(numbers: List<Int>): String {
    var str = ""
    for(num in numbers) {
        str = if(num < 10) "$str 0$num" else "$str $num"
    }
    return str.substring(1)
}

fun convertNumbersToStringWithBonus(ticketNums: Pair<List<Int>, Int>): String {
    val str = convertNumbersToStringWithoutBonus(ticketNums.first)
    return if(ticketNums.second < 10) "$str + 0${ticketNums.second}" else "$str + ${ticketNums.second}"
}

fun getMatchCountString(type: LotteryType, count: Int, isBonus: Boolean): String {
    return when(type) {
        POWERBALL, MEGA_MILLIONS -> {
            when(count) {
                5 -> if(isBonus) "5+" else "5"
                4 -> if(isBonus) "4+" else "4"
                3 -> if(isBonus) "3+" else "3"
                2 -> if(isBonus) "2+" else ":("
                1 -> if(isBonus) "1+" else ":("
                0 -> if(isBonus) "+" else ":("
                else -> ""
            }
        }

        LOTTO_MAX -> {
            when(count) {
                7 -> "7"
                6 -> if(isBonus) "6+" else "6"
                5 -> if(isBonus) "5+" else "5"
                4 -> if(isBonus) "4+" else "4"
                3 -> if(isBonus) "3+" else "3"
                2, 1, 0 -> ":("
                else -> ""
            }
        }

        LOTTO_649 -> {
            when(count) {
                6 -> "6"
                5 -> if(isBonus) "5+" else "5"
                4 -> "4"
                3 -> "3"
                2 -> if(isBonus) "2+" else "2"
                1, 0 -> ":("
                else -> ""
            }
        }
    }
}

fun getResultUS(context: Context, numsToColor: Pair<List<Int>, Int>, numsToCompare: Pair<List<Int>, Int>):
        Pair<SpannableString, Pair<Int, Boolean>> {

    //color for matches
    val coloredTicketNumbers = SpannableString(convertNumbersToStringWithBonus(numsToColor))
    var countMatches = 0
    for((index, num) in numsToColor.first.withIndex()){
        if(numsToCompare.first.contains(num)){
            coloredTicketNumbers.setSpan(
                ForegroundColorSpan(getColor(context, R.color.match_color)),
                index * 3,
                index * 3 + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            countMatches++
        }
    }

    //color for bonus
    var isBonus = false
    if(numsToColor.second == numsToCompare.second) {
        coloredTicketNumbers.setSpan(
            ForegroundColorSpan(getColor(context, R.color.match_bonus_color)),
            coloredTicketNumbers.length - 2,
            coloredTicketNumbers.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        isBonus = true
    }

    return Pair(coloredTicketNumbers, Pair(countMatches, isBonus))
}

private fun getResultUSWithMatchCountString(context: Context, ticketNums: Pair<List<Int>, Int>, winNums: Pair<List<Int>, Int>):
        Pair<SpannableString, String> {
    val result = getResultUS(context, ticketNums, winNums)

    return Pair(result.first, getMatchCountString(POWERBALL, result.second.first, result.second.second))
}

private fun getResultCanadian(context: Context, numsToColor: List<Int>, numsToCompare: Pair<List<Int>, Int>, type: LotteryType):
        Pair<SpannableString, String> {

    val coloredTicketNumbers = SpannableString(convertNumbersToStringWithoutBonus(numsToColor))
    var countMatches = 0
    var isBonus = false

    for((index, num) in numsToColor.withIndex()){
        //color for matches
        if(numsToCompare.first.contains(num)){
            coloredTicketNumbers.setSpan(
                ForegroundColorSpan(getColor(context, R.color.match_color)),
                index * 3,
                index * 3 + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            countMatches++
        }

        //color for bonus
        if(num == numsToCompare.second) {
            coloredTicketNumbers.setSpan(
                ForegroundColorSpan(getColor(context, R.color.match_bonus_color)),
                index * 3,
                index * 3 + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            isBonus = true
        }
    }

    return Pair(coloredTicketNumbers, getMatchCountString(type, countMatches, isBonus))
}

fun getNewTicketWithResult(context: Context, type: LotteryType, winNums: Pair<List<Int>, Int>):
        Pair<SpannableString, String> {
    return when(type) {
        POWERBALL, MEGA_MILLIONS ->
            getResultUSWithMatchCountString(context, generateTicketWithBonus(type), winNums)
        LOTTO_MAX, LOTTO_649 ->
            getResultCanadian(context, generateTicketWithoutBonus(type), winNums, type)
    }
}

fun String.toListOfInt() = trim()
    .splitToSequence(' ')
    .filter { it.isNotEmpty() }
    .toList()
    .map { it.toInt() }

fun copyToClipboard(context: Context, text: String) {
    val clipboard: ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("ticket numbers", text)
    clipboard.setPrimaryClip(clip)
}