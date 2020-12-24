package org.dave.lotterysimulatorwithstat.fragment

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.activity.MainActivity
import org.dave.lotterysimulatorwithstat.testUtil.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainViewPagerFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun test_views_displayed() {
        launchFragmentInHiltContainer<MainViewPagerFragment>()
        onView(withId(R.id.main_view_pager_fragment_toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.lottery_type_dropdown)).check(matches(isDisplayed()))
        onView(withId(R.id.tab_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.pager)).check(matches(isDisplayed()))
        onView(withId(R.id.ticket_history)).check(matches(isDisplayed()))
    }

    @Test
    fun test_lottery_type_dropdown() {
        launchFragmentInHiltContainer<MainViewPagerFragment>()
        onView(withId(R.id.lottery_type_dropdown)).perform(click())
        onView(withText(R.string.lottery_name_powerball))
            .inRoot(RootMatchers.isPlatformPopup())
            .check(matches(isDisplayed()))
        onView(withText(R.string.lottery_name_mega_millions))
            .inRoot(RootMatchers.isPlatformPopup())
            .check(matches(isDisplayed()))
        onView(withText(R.string.lottery_name_lotto_max))
            .inRoot(RootMatchers.isPlatformPopup())
            .check(matches(isDisplayed()))
        onView(withText(R.string.lottery_name_lotto_649))
            .inRoot(RootMatchers.isPlatformPopup())
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_view_pager_tabs() {
        launchFragmentInHiltContainer<MainViewPagerFragment>()
        onView(withId(R.id.tv_random_numbers)).check(matches(isDisplayed()))
        onView(withText(R.string.tab_title_simulation)).perform(click())
        onView(withId(R.id.tv_winning_numbers)).check(matches(isDisplayed()))
        onView(withText(R.string.tab_title_stat)).perform(click())
        onView(withId(R.id.toggle_stat_type)).check(matches(isDisplayed()))
        onView(withText(R.string.tab_title_generate)).perform(click())
        onView(withId(R.id.tv_random_numbers)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigate_to_ticket_history() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.ticket_history)).perform(click())
        onView(withId(R.id.ticket_history_list)).check(matches(isDisplayed()))
        activityScenario.close()
    }
}