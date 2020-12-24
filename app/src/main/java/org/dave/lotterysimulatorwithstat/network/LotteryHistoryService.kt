package org.dave.lotterysimulatorwithstat.network

import android.content.Context
import android.net.ConnectivityManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private fun hasNetwork(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}

interface LotteryHistoryService {
    @GET(POWERBALL_URL)
    suspend fun getPowerballHistory(): List<PastPBWinningNumber>

    @GET(MEGA_MILLIONS_URL)
    suspend fun getMegaMillionsHistory(): List<PastMMWinningNumber>

    companion object {
        private const val BASE_URL = "https://data.ny.gov/resource/"
        private const val POWERBALL_URL = "d6yy-54nr.json"
        private const val MEGA_MILLIONS_URL = "5xaw-6ayf.json"
        private const val cacheSize: Long = 5 * 1024 * 1024

        fun create(context: Context): LotteryHistoryService {

            val okHttpClient = OkHttpClient.Builder()
                .cache(Cache(context.cacheDir, cacheSize))
                .addInterceptor {
                    var request = it.request()
                    request = if (hasNetwork(context))
                        request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                    else
                        request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                    it.proceed(request)
                }
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build()
                .create(LotteryHistoryService::class.java)
        }
    }
}