package org.dave.lotterysimulatorwithstat.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.dave.lotterysimulatorwithstat.network.LotteryHistoryService
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideLotteryHistoryService(@ApplicationContext context: Context): LotteryHistoryService {
        return LotteryHistoryService.create(context)
    }
}