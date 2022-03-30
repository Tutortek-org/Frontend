package com.tutortekorg.tutortek

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.tutortekorg.tutortek.onboarding.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class OnboardingTests {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun goThroughOnBoarding() {
        onView(withId(R.id.btn_onboarding_action)).perform(click())
        onView(withId(R.id.btn_onboarding_action)).perform(click())
        onView(withId(R.id.btn_onboarding_action)).perform(click())
        onView(withId(R.id.img_login_icon)).check(matches(isDisplayed()))
    }
}
