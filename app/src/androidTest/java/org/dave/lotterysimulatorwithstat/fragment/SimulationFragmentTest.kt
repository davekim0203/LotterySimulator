package org.dave.lotterysimulatorwithstat.fragment

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.adapter.TicketAdapter
import org.dave.lotterysimulatorwithstat.testUtil.RecyclerViewItemCountAssertion
import org.dave.lotterysimulatorwithstat.testUtil.launchFragmentInHiltContainer
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SimulationFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
        launchFragmentInHiltContainer<SimulationFragment>()
    }

    @Test
    fun test_views_displayed() {
        onView(withId(R.id.tv_winning_numbers)).check(matches(isDisplayed()))
        onView(withId(R.id.checkBox_fix_winning_numbers)).check(matches(isDisplayed()))
        onView(withId(R.id.ticket_numbers_to_generate)).check(matches(isDisplayed()))
        onView(withId(R.id.ticket_list)).check(matches(isDisplayed()))
        onView(withId(R.id.button_simulate)).check(matches(isDisplayed()))
    }

    @Test
    fun test_winning_numbers() {
        //default text
        onView(withId(R.id.tv_winning_numbers)).check(matches(withText(R.string.start_simulation_text)))
        onView(withId(R.id.button_simulate)).perform(click())
        onView(withId(R.id.tv_winning_numbers)).check(matches(not(withText(R.string.start_simulation_text))))
    }

    @Test
    fun test_checkbox_fix_winning_numbers() {
        //default state
        onView(withId(R.id.checkBox_fix_winning_numbers)).check(matches(isChecked()))
        onView(withId(R.id.checkBox_fix_winning_numbers)).check(matches(not(isEnabled())))

        onView(withId(R.id.button_simulate)).perform(click())
        onView(withId(R.id.checkBox_fix_winning_numbers)).check(matches(isChecked()))
        onView(withId(R.id.checkBox_fix_winning_numbers)).check(matches(isEnabled()))
        val winningNumbersText1 = getTextFromView(onView(withId(R.id.tv_winning_numbers)))

        //if checked, winning numbers are fixed
        onView(withId(R.id.button_simulate)).perform(click())
        val winningNumbersText2 = getTextFromView(onView(withId(R.id.tv_winning_numbers)))
        assertEquals(winningNumbersText1, winningNumbersText2)

        //if not checked, winning numbers are not fixed
        onView(withId(R.id.checkBox_fix_winning_numbers)).perform(click())
        onView(withId(R.id.checkBox_fix_winning_numbers)).check(matches(not(isChecked())))
        onView(withId(R.id.button_simulate)).perform(click())
        val winningNumbersText3 = getTextFromView(onView(withId(R.id.tv_winning_numbers)))
        assertNotEquals(winningNumbersText2, winningNumbersText3)
    }

    @Test
    fun test_number_of_tickets_to_generate() {
        //default number
        onView(withId(R.id.et_ticket_numbers)).check(matches(withText("10")))
        onView(withId(R.id.button_simulate)).perform(click())
        onView(withId(R.id.ticket_list)).check(RecyclerViewItemCountAssertion(10))

        //change number
        onView(withId(R.id.et_ticket_numbers)).perform(
            click(),
            replaceText("15"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.et_ticket_numbers)).check(matches(withText("15")))
        onView(withId(R.id.button_simulate)).perform(click())
        onView(withId(R.id.ticket_list)).check(RecyclerViewItemCountAssertion(15))
    }

    @Test
    fun test_number_of_tickets_to_generate_no_input() {
        onView(withId(R.id.et_ticket_numbers)).perform(
            click(),
            replaceText(""),
            closeSoftKeyboard()
        )
        onView(withId(R.id.button_simulate)).perform(click())
        onView(withText(R.string.num_of_tickets_warning_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.num_of_tickets_warning_dialog_message_no_number)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withText(R.string.dialog_ok_button)).check(matches(isDisplayed()))
    }

    @Test
    fun test_number_of_tickets_to_generate_zero_input() {
        onView(withId(R.id.et_ticket_numbers)).perform(
            click(),
            replaceText("0"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.button_simulate)).perform(click())
        onView(withText(R.string.num_of_tickets_warning_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.num_of_tickets_warning_dialog_message_greater_than_0)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withText(R.string.dialog_ok_button)).check(matches(isDisplayed()))
    }

    @Test
    fun test_long_press_on_winning_numbers() {
        onView(withId(R.id.button_simulate)).perform(click())
        onView(withId(R.id.tv_winning_numbers)).perform(longClick())
        onView(withText(R.string.menu_copy_to_clipboard)).check(matches(isDisplayed()))
    }

    @Test
    fun test_long_press_on_list_numbers() {
        onView(withId(R.id.button_simulate)).perform(click())
        onView(withId(R.id.ticket_list)).perform(
            actionOnItemAtPosition<TicketAdapter.ViewHolder>(
                3,
                longClick()
            )
        )
        onView(withText(R.string.menu_copy_to_clipboard)).check(matches(isDisplayed()))
    }

    private fun getTextFromView(viewInteraction: ViewInteraction): String? {
        val stringHolder = arrayOf<String?>(null)
        viewInteraction.perform(object : ViewAction {
            override fun getConstraints() = isAssignableFrom(TextView::class.java)

            override fun getDescription() = "Get text from View: ${stringHolder[0]}"

            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView
                stringHolder[0] = tv.text.toString()
            }
        })
        return stringHolder[0]
    }
}