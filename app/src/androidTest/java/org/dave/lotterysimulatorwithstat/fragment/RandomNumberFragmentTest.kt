package org.dave.lotterysimulatorwithstat.fragment

import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.activity.MainActivity
import org.dave.lotterysimulatorwithstat.util.EspressoIdlingResource
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class RandomNumberFragmentTest {

    private val hiltRule = HiltAndroidRule(this)
    private val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val rule = RuleChain
        .outerRule(hiltRule)
        .around(activityTestRule)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun test_views_displayed() {
        onView(withId(R.id.tv_random_numbers)).check(matches(isDisplayed()))
        onView(withId(R.id.checkBox_get_past_matches)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_past_matches_description)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_generate)).check(matches(isDisplayed()))

        //by default, not displayed
        onView(withId(R.id.tv_number_of_wins)).check(matches(not(isDisplayed())))
        onView(withId(R.id.past_matches_list)).check(matches(not(isDisplayed())))
    }

    @Test
    fun test_generated_numbers() {
        //default text
        onView(withId(R.id.tv_random_numbers)).check(matches(withText(R.string.default_numbers_text)))
        onView(withId(R.id.btn_generate)).perform(click())
        onView(withId(R.id.tv_random_numbers)).check(matches(not(withText(R.string.default_numbers_text))))
    }

    @Test
    fun test_checkbox_get_past_matches_isEnabled() {
        selectDropDown(R.string.lottery_name_mega_millions)
        onView(withId(R.id.checkBox_get_past_matches)).check(matches(isEnabled()))
        selectDropDown(R.string.lottery_name_powerball)
        onView(withId(R.id.checkBox_get_past_matches)).check(matches(isEnabled()))
        selectDropDown(R.string.lottery_name_lotto_max)
        onView(withId(R.id.checkBox_get_past_matches)).check(matches(not(isEnabled())))
        selectDropDown(R.string.lottery_name_lotto_649)
        onView(withId(R.id.checkBox_get_past_matches)).check(matches(not(isEnabled())))
    }

    @Test
    fun test_checkbox_get_past_matches_when_checked() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        //default state
        onView(withId(R.id.checkBox_get_past_matches)).check(matches(isChecked()))
        onView(withId(R.id.checkBox_get_past_matches)).check(matches(isEnabled()))

        onView(withId(R.id.btn_generate)).perform(click())
        onView(withId(R.id.past_matches_list)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_number_of_wins)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_past_matches_description)).check(matches(not(isDisplayed())))

        //uncheck the checkbox
        onView(withId(R.id.checkBox_get_past_matches)).perform(click())
        onView(withId(R.id.btn_generate)).perform(click())
        onView(withId(R.id.past_matches_list)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_number_of_wins)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_past_matches_description)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_past_matches_description)).check(matches(withText(R.string.past_matches_description)))

        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_past_matches_list_visibility_by_lottery_type_change() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        //click generate button
        onView(withId(R.id.btn_generate)).perform(click())
        onView(withId(R.id.past_matches_list)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_number_of_wins)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_past_matches_description)).check(matches(not(isDisplayed())))

        //select mega millions
        selectDropDown(R.string.lottery_name_mega_millions)
        onView(withId(R.id.past_matches_list)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_number_of_wins)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_past_matches_description)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_past_matches_description)).check(matches(withText(R.string.past_matches_description)))

        //click generate button again
        onView(withId(R.id.btn_generate)).perform(click())
        onView(withId(R.id.past_matches_list)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_number_of_wins)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_past_matches_description)).check(matches(not(isDisplayed())))

        //select lotto max
        selectDropDown(R.string.lottery_name_lotto_max)
        onView(withId(R.id.past_matches_list)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_number_of_wins)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_past_matches_description)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_past_matches_description)).check(matches(withText(R.string.api_not_available_for_lotto_max_and_649)))

        //click generate button again
        onView(withId(R.id.btn_generate)).perform(click())
        onView(withId(R.id.past_matches_list)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_number_of_wins)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_past_matches_description)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_past_matches_description)).check(matches(withText(R.string.api_not_available_for_lotto_max_and_649)))

        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    private fun selectDropDown(@StringRes lotteryText: Int) {
        onView(withId(R.id.lottery_type_dropdown)).perform(click())
        onView(withText(lotteryText))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())
    }
}