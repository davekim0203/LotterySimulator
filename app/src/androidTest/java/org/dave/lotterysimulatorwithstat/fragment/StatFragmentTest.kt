package org.dave.lotterysimulatorwithstat.fragment

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.testUtil.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class StatFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
        launchFragmentInHiltContainer<StatFragment>()
    }

    @Test
    fun test_views_displayed() {
        onView(withId(R.id.toggle_stat_type)).check(matches(isDisplayed()))
        onView(withId(R.id.toggle_pie)).check(matches(isDisplayed()))
        onView(withId(R.id.toggle_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.stat_container)).check(matches(isDisplayed()))
    }

    @Test
    fun test_toggle_chart_fragments() {
        //by default, pie chart is displayed
        onView(withId(R.id.pie_chart)).check(matches(isDisplayed()))
        onView(withId(R.id.bar_chart)).check(doesNotExist())

        onView(withId(R.id.toggle_bar)).perform(click())
        onView(withId(R.id.bar_chart)).check(matches(isDisplayed()))
        onView(withId(R.id.pie_chart)).check(doesNotExist())

        onView(withId(R.id.toggle_pie)).perform(click())
        onView(withId(R.id.pie_chart)).check(matches(isDisplayed()))
        onView(withId(R.id.bar_chart)).check(doesNotExist())
    }
}