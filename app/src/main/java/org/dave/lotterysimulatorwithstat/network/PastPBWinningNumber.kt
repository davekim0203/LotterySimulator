package org.dave.lotterysimulatorwithstat.network

import com.squareup.moshi.Json

/*
* data class for Powerball past winning numbers
*/
data class PastPBWinningNumber(
    @Json(name = "draw_date")
    val drawDate: String,

    @Json(name = "winning_numbers")
    val winningNumbers: String
    )