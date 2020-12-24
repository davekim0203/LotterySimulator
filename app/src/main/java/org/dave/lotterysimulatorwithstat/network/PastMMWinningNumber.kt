package org.dave.lotterysimulatorwithstat.network

import com.squareup.moshi.Json

/*
* data class for Mega Millions past winning numbers
*/
data class PastMMWinningNumber(
    @Json(name = "draw_date")
    val drawDate: String,

    @Json(name = "winning_numbers")
    val winningNumbers: String,

    @Json(name = "mega_ball")
    val bonus: Int
    )