package org.dave.lotterysimulatorwithstat.util

import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.adapter.PastMatchAdapter
import org.dave.lotterysimulatorwithstat.adapter.PastWinningNumber
import org.dave.lotterysimulatorwithstat.adapter.TicketAdapter
import org.dave.lotterysimulatorwithstat.database.LotteryType
import org.dave.lotterysimulatorwithstat.database.Ticket


@BindingAdapter("lotteryType")
fun TextView.setLotteryTypeText(item: Ticket?) {
    item?.let {
        text = resources.getString(item.type.shortNameId)
    }
}

@BindingAdapter("ticketNumbers")
fun TextView.setTicketNumbersText(item: Ticket?) {
    item?.let {
        text = HtmlCompat.fromHtml(item.numbers, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

@BindingAdapter("result")
fun TextView.setTicketResultText(item: Ticket?) {
    item?.let {
        text = item.matchCount
    }
}

@BindingAdapter("drawDateMonth")
fun TextView.setDrawDateMonthText(item: PastWinningNumber?) {
    item?.let {
        val monthInNumber = item.drawDate.substring(5, 7)
        val day = item.drawDate.substring(8, 10)
        val monthInText = when(monthInNumber) {
            "01" -> resources.getString(R.string.past_match_month_january)
            "02" -> resources.getString(R.string.past_match_month_february)
            "03" -> resources.getString(R.string.past_match_month_march)
            "04" -> resources.getString(R.string.past_match_month_april)
            "05" -> resources.getString(R.string.past_match_month_may)
            "06" -> resources.getString(R.string.past_match_month_june)
            "07" -> resources.getString(R.string.past_match_month_july)
            "08" -> resources.getString(R.string.past_match_month_august)
            "09" -> resources.getString(R.string.past_match_month_september)
            "10" -> resources.getString(R.string.past_match_month_october)
            "11" -> resources.getString(R.string.past_match_month_november)
            "12" -> resources.getString(R.string.past_match_month_december)
            else -> resources.getString(R.string.empty_string)
        }

        text = String.format(resources.getString(R.string.past_match_month_day), monthInText, day)
    }
}

@BindingAdapter("drawDateYear")
fun TextView.setDrawDateYearText(item: PastWinningNumber?) {
    item?.let {
        text = item.drawDate.substring(0, 4)
    }
}

@BindingAdapter("winningNumbers")
fun TextView.setWinningNumbersText(item: PastWinningNumber?) {
    item?.let {
        text = HtmlCompat.fromHtml(item.winningNumbers, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

@BindingAdapter("result")
fun TextView.setMatchCountText(item: PastWinningNumber?) {
    item?.let {
        //POWERBALL is hardcoded because:
        // 1. only US lotteries use this listview
        // 2. getMatchCountString() works same for both PowerBall and Mega Millions
        text = getMatchCountString(
            LotteryType.POWERBALL,
            item.matchCount.first,
            item.matchCount.second
        )
    }
}

@BindingAdapter("pastMatchDescriptionText")
fun TextView.setPastMatchDescriptionText(item: LotteryType?) {
    item?.let {
        text = when (item) {
            LotteryType.POWERBALL, LotteryType.MEGA_MILLIONS -> resources.getString(R.string.past_matches_description)
            else -> resources.getString(R.string.api_not_available_for_lotto_max_and_649)
        }
    }
}

@BindingAdapter("networkError")
fun TextView.setNetworkErrorText(item: Boolean?) {
    item?.let {
        if(it) {
            text = resources.getString(R.string.network_error)
        }
    }
}

@BindingAdapter("simulationWinningNumbersText")
fun TextView.setSimulationWinningNumbersText(item: Pair<List<Int>, Int>?) {
    text = if(item == null) {
        resources.getString(R.string.start_simulation_text)
    } else {
        convertNumbersToStringWithBonus(item)
    }
}

@BindingAdapter("tickets")
fun setTicketList(listView: RecyclerView, tickets: List<Ticket>?) {
    tickets?.let {
        (listView.adapter as TicketAdapter).data = it
    }
}

@BindingAdapter("pastMatches")
fun setPastMatchList(listView: RecyclerView, pastMatches: List<PastWinningNumber>?) {
    pastMatches?.let {
        (listView.adapter as PastMatchAdapter).data = it
    }
}

//How to data bind long press
//@BindingAdapter("onLongPressed")
//fun View.setMyOnLongClickListener(
//    func : () -> Unit
//) {
//    setOnLongClickListener {
//        func()
//        return@setOnLongClickListener true
//    }
//}