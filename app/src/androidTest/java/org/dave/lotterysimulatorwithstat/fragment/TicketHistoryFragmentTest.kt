package org.dave.lotterysimulatorwithstat.fragment

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.adapter.TicketAdapter
import org.dave.lotterysimulatorwithstat.testUtil.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TicketHistoryFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
        launchFragmentInHiltContainer<TicketHistoryFragment>()
    }

    @Test
    fun test_views_displayed() {
        onView(withId(R.id.ticket_history_list)).check(matches(isDisplayed()))
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.clear_history)).check(matches(isDisplayed()))
        onView(withId(R.id.history_info)).check(matches(isDisplayed()))
    }

    @Test
    fun test_toolbar_title() {
        onView(withText(R.string.ticket_history_fragment_title)).check(matches(withParent(withId(R.id.toolbar))))
    }

    @Test
    fun test_clear_dialog() {
        onView(withId(R.id.clear_history)).perform(click())
        onView(withText(R.string.clear_data_confirm_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.clear_data_confirm_dialog_message)).check(matches(isDisplayed()))
        onView(withText(R.string.clear_data_confirm_dialog_positive_button)).check(matches(isDisplayed()))
        onView(withText(R.string.dialog_negative_button)).check(matches(isDisplayed()))
    }

    @Test
    fun test_info_dialog() {
        onView(withId(R.id.history_info)).perform(click())
        onView(withText(R.string.info_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.info_dialog_message)).check(matches(isDisplayed()))
        onView(withText(R.string.info_dialog_positive_button)).check(matches(isDisplayed()))
    }

    @Test
    fun test_long_press_on_list_numbers() {
        onView(withId(R.id.ticket_history_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<TicketAdapter.ViewHolder>(
                0,
                ViewActions.longClick()
            )
        )
        onView(withText(R.string.menu_copy_to_clipboard)).check(matches(isDisplayed()))
    }
}