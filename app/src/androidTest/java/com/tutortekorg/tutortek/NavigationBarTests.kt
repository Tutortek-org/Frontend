package com.tutortekorg.tutortek

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
@LargeTest
class NavigationBarTests {

    @get:Rule
    val rule = ActivityScenarioRule(HomeActivity::class.java)

    @Test
    fun checkForNavBar() {
        onView(withId(R.id.navigation_card_view)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToSettings() {
        onView(withId(R.id.settingsFragment)).perform(click())
        onView(withId(R.id.btn_logout)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToProfile() {
        onView(withId(R.id.profileFragment)).perform(click())
        onView(withId(R.id.btn_edit_profile)).check(matches(isDisplayed()))
    }
}
