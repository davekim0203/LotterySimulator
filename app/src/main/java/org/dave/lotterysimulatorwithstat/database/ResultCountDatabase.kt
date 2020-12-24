package org.dave.lotterysimulatorwithstat.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.database.LotteryType.*
import java.util.concurrent.Executors

@Database(entities = [ResultCount::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ResultCountDatabase : RoomDatabase() {

    abstract fun resultCountDao(): ResultCountDao

    companion object {
        @Volatile
        private var INSTANCE: ResultCountDatabase? = null

        fun getDatabase(
            context: Context
        ): ResultCountDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ResultCountDatabase::class.java,
                    "result_count_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            insertInitialData(context)
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun insertInitialData(context: Context) {
            val initialData = listOf(
                ResultCount(POWERBALL, context.getString(R.string.match_count_five_bonus), 0),
                ResultCount(POWERBALL, context.getString(R.string.match_count_five), 0),
                ResultCount(POWERBALL, context.getString(R.string.match_count_four_bonus), 0),
                ResultCount(POWERBALL, context.getString(R.string.match_count_four), 0),
                ResultCount(POWERBALL, context.getString(R.string.match_count_three_bonus), 0),
                ResultCount(POWERBALL, context.getString(R.string.match_count_three), 0),
                ResultCount(POWERBALL, context.getString(R.string.match_count_two_bonus), 0),
                ResultCount(POWERBALL, context.getString(R.string.match_count_one_bonus), 0),
                ResultCount(POWERBALL, context.getString(R.string.match_count_none_bonus), 0),
                ResultCount(POWERBALL, context.getString(R.string.match_count_else), 0),
                ResultCount(MEGA_MILLIONS, context.getString(R.string.match_count_five_bonus), 0),
                ResultCount(MEGA_MILLIONS, context.getString(R.string.match_count_five), 0),
                ResultCount(MEGA_MILLIONS, context.getString(R.string.match_count_four_bonus), 0),
                ResultCount(MEGA_MILLIONS, context.getString(R.string.match_count_four), 0),
                ResultCount(MEGA_MILLIONS, context.getString(R.string.match_count_three_bonus), 0),
                ResultCount(MEGA_MILLIONS, context.getString(R.string.match_count_three), 0),
                ResultCount(MEGA_MILLIONS, context.getString(R.string.match_count_two_bonus), 0),
                ResultCount(MEGA_MILLIONS, context.getString(R.string.match_count_one_bonus), 0),
                ResultCount(MEGA_MILLIONS, context.getString(R.string.match_count_none_bonus), 0),
                ResultCount(MEGA_MILLIONS, context.getString(R.string.match_count_else), 0),
                ResultCount(LOTTO_MAX, context.getString(R.string.match_count_seven), 0),
                ResultCount(LOTTO_MAX, context.getString(R.string.match_count_six_bonus), 0),
                ResultCount(LOTTO_MAX, context.getString(R.string.match_count_six), 0),
                ResultCount(LOTTO_MAX, context.getString(R.string.match_count_five_bonus), 0),
                ResultCount(LOTTO_MAX, context.getString(R.string.match_count_five), 0),
                ResultCount(LOTTO_MAX, context.getString(R.string.match_count_four_bonus), 0),
                ResultCount(LOTTO_MAX, context.getString(R.string.match_count_four), 0),
                ResultCount(LOTTO_MAX, context.getString(R.string.match_count_three_bonus), 0),
                ResultCount(LOTTO_MAX, context.getString(R.string.match_count_three), 0),
                ResultCount(LOTTO_MAX, context.getString(R.string.match_count_else), 0),
                ResultCount(LOTTO_649, context.getString(R.string.match_count_six), 0),
                ResultCount(LOTTO_649, context.getString(R.string.match_count_five_bonus), 0),
                ResultCount(LOTTO_649, context.getString(R.string.match_count_five), 0),
                ResultCount(LOTTO_649, context.getString(R.string.match_count_four), 0),
                ResultCount(LOTTO_649, context.getString(R.string.match_count_three), 0),
                ResultCount(LOTTO_649, context.getString(R.string.match_count_two_bonus), 0),
                ResultCount(LOTTO_649, context.getString(R.string.match_count_two), 0),
                ResultCount(LOTTO_649, context.getString(R.string.match_count_else), 0)
            )

            ioThread {
                getDatabase(context).resultCountDao().insertAll(initialData)
            }
        }
    }
}

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}
