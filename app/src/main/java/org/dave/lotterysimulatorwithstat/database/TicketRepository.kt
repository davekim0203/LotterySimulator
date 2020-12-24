package org.dave.lotterysimulatorwithstat.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketRepository @Inject constructor(private val ticketDao: TicketDao) {

    val allTickets: LiveData<List<Ticket>> = ticketDao.getAllTickets()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(tickets: List<Ticket>) {
        ticketDao.insertAll(tickets)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun clearAllTickets() {
        ticketDao.clear()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteOldTickets() {
        ticketDao.deleteOldTickets()
    }
}
