package org.dave.lotterysimulatorwithstat.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface TicketDao {

    @Insert
    fun insertAll(tickets: List<Ticket>)

    @Query("SELECT * FROM ticket_table ORDER BY ticketId DESC")
    fun getAllTickets(): LiveData<List<Ticket>>

    /**
     * deleteOldTickets() removes old tickets if there are more than 10000 tickets in database
     * by FIFO (first in first out)
     */
    @Query("DELETE FROM ticket_table where ticketId NOT IN (SELECT ticketId from ticket_table ORDER BY ticketId DESC LIMIT 10000)")
    fun deleteOldTickets()

    @Query("DELETE FROM ticket_table")
    fun clear()
}