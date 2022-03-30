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
class ProfileTests {

    @get:Rule
    val rule = ActivityScenarioRule(HomeActivity::class.java)

    @Test
    fun navigateToDeleteMenu() {
        onView(withId(R.id.profileFragment)).perform(click())
        onView(withId(R.id.btn_delete_account)).perform(click())
        onView(withId(R.id.txt_input_password_delete)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToEditMenu() {
        onView(withId(R.id.profileFragment)).perform(click())
        onView(withId(R.id.btn_edit_profile)).perform(click())
        onView(withId(R.id.btn_change_profile_data)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToProfileDataEditForm() {
        onView(withId(R.id.profileFragment)).perform(click())
        onView(withId(R.id.btn_edit_profile)).perform(click())
        onView(withId(R.id.btn_change_profile_data)).perform(click())
        onView(withId(R.id.profile_txt_input_name)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToChangePasswordForm() {
        onView(withId(R.id.profileFragment)).perform(click())
        onView(withId(R.id.btn_edit_profile)).perform(click())
        onView(withId(R.id.btn_change_password)).perform(click())
        onView(withId(R.id.txt_input_current_password)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToChangeProfilePhotoScreen() {
        onView(withId(R.id.profileFragment)).perform(click())
        onView(withId(R.id.btn_edit_profile)).perform(click())
        onView(withId(R.id.btn_profile_photo)).perform(click())
        onView(withId(R.id.profile_photo)).check(matches(isDisplayed()))
    }
}
