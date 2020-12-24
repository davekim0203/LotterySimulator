package org.dave.lotterysimulatorwithstat.database

import android.content.Context
import androidx.room.*

@Database(entities = [Ticket::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TicketDatabase : RoomDatabase() {

    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile
        private var INSTANCE: TicketDatabase? = null

        fun getDatabase(context: Context): TicketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TicketDatabase::class.java,
                    "ticket_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
