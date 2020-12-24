package org.dave.lotterysimulatorwithstat.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.dave.lotterysimulatorwithstat.database.ResultCountDao
import org.dave.lotterysimulatorwithstat.database.ResultCountDatabase
import org.dave.lotterysimulatorwithstat.database.TicketDao
import org.dave.lotterysimulatorwithstat.database.TicketDatabase
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideTicketDatabase(@ApplicationContext context: Context): TicketDatabase {
        return TicketDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideResultCountDatabase(@ApplicationContext context: Context): ResultCountDatabase {
        return ResultCountDatabase.getDatabase(context)
    }

    @Provides
    fun provideTicketDao(ticketDatabase: TicketDatabase): TicketDao {
        return ticketDatabase.ticketDao()
    }

    @Provides
    fun provideResultCountDao(resultCountDatabase: ResultCountDatabase): ResultCountDao {
        return resultCountDatabase.resultCountDao()
    }
}