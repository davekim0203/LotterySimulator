package org.dave.lotterysimulatorwithstat.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.dave.lotterysimulatorwithstat.testUtil.getOrAwaitValue
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TicketDaoTest {

    private lateinit var ticketDatabase: TicketDatabase
    private lateinit var ticketDao: TicketDao
    private val ticket1 = Ticket(type = LotteryType.POWERBALL, numbers = "01 02 03 04 05 + 06", matchCount = ":(")
    private val ticket2 = Ticket(type = LotteryType.POWERBALL, numbers = "07 08 09 10 11 + 12", matchCount = ":(")
    private val ticket3 = Ticket(type = LotteryType.POWERBALL, numbers = "11 12 13 14 15 + 16", matchCount = "+")
    private val ticket4 = Ticket(type = LotteryType.POWERBALL, numbers = "17 18 19 20 21 + 22", matchCount = "1+")
    private val ticket5 = Ticket(type = LotteryType.MEGA_MILLIONS, numbers = "01 02 03 04 05 + 06", matchCount = ":(")
    private val ticket6 = Ticket(type = LotteryType.MEGA_MILLIONS, numbers = "07 08 09 10 11 + 12", matchCount = "+")
    private val testData = listOf(ticket1, ticket2, ticket3, ticket4, ticket5, ticket6)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupDatabase() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        ticketDatabase =
            Room.inMemoryDatabaseBuilder(context, TicketDatabase::class.java).build()
        ticketDao = ticketDatabase.ticketDao()

        ticketDao.insertAll(testData)
    }

    @After
    fun closeDatabase() {
        ticketDatabase.close()
    }

    @Test
    fun test_getAllTickets() {
        val allTickets = ticketDao.getAllTickets().getOrAwaitValue()

        //simulate autoGenerate
        ticket1.ticketId = 1L
        ticket2.ticketId = 2L
        ticket3.ticketId = 3L
        ticket4.ticketId = 4L
        ticket5.ticketId = 5L
        ticket6.ticketId = 6L

        val expected = listOf(ticket6, ticket5, ticket4, ticket3, ticket2, ticket1)     //right order
        assertEquals(expected, allTickets)
    }

    /**
     * Test steps:
     * Part 1
     *  1. Insert 9000 tickets to database -> 9006 tickets in database
     *  2. Call deleteOldTickets()
     *  3. Check the last row is still the first ticket inserted (ticket1 from setup)
     *
     * Part 2
     *  4. Insert 1000 more tickets to database -> 10006 tickets in database
     *  5. Call deleteOldTickets()
     *  6. Check the number of rows is 10000
     *  7. Check the last row is not the first ticket inserted anymore
     */
    @Test
    fun test_deleteOldTickets() {
        val fakeTicket = Ticket(type = LotteryType.POWERBALL, numbers = "31 32 33 34 35 + 36", matchCount = ":(")

        //Part 1
        val fakeTicketList1 = mutableListOf<Ticket>()
        for (i in 0..8999) {
            fakeTicketList1.add(i, fakeTicket)
        }
        ticketDao.insertAll(fakeTicketList1)
        ticketDao.deleteOldTickets()
        val allTickets1 = ticketDao.getAllTickets().getOrAwaitValue()
        val ticketFromLastRow1 = allTickets1[allTickets1.size - 1]
        assertEquals(ticket1.numbers, ticketFromLastRow1.numbers)

        //Part 2
        val fakeTicketList2 = mutableListOf<Ticket>()
        for (i in 0..999) {
            fakeTicketList2.add(i, fakeTicket)
        }
        ticketDao.insertAll(fakeTicketList1)
        ticketDao.deleteOldTickets()
        val allTickets2 = ticketDao.getAllTickets().getOrAwaitValue()
        assertEquals(10000, allTickets2.size)

        val ticketFromLastRow2 = allTickets2[allTickets2.size - 1]
        assertEquals(fakeTicket.numbers, ticketFromLastRow2.numbers)
    }

    @Test
    fun test_clear() {
        ticketDao.clear()
        val allTickets = ticketDao.getAllTickets().getOrAwaitValue()
        val expected = listOf<Ticket>()
        assertEquals(expected, allTickets)
    }
}