package com.tutortekorg.tutortek

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SettingsTests {

    @get:Rule
    val rule = ActivityScenarioRule(HomeActivity::class.java)

    @Test
    fun navigateToAppInfo() {
        onView(withId(R.id.settingsFragment)).perform(click())
        onView(withId(R.id.btn_app_info)).perform(click())
        onView(withId(R.id.contact_information)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToBugReportForm() {
        onView(withId(R.id.settingsFragment)).perform(click())
        onView(withId(R.id.btn_report_bug)).perform(click())
        onView(withId(R.id.txt_input_bug_name)).check(matches(isDisplayed()))
    }
}
