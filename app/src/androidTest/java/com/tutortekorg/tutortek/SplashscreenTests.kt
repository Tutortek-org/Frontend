package com.tutortekorg.tutortek

import androidx.test.espresso.Espresso.onView
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
class SplashscreenTests {

    @get:Rule
    val rule = ActivityScenarioRule(SplashscreenActivity::class.java)

    @Test
    fun checkForSplashscreen() {
        onView(withId(R.id.img_splashscreen)).check(matches(isDisplayed()))
    }
}
