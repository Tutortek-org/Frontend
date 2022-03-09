package com.tutortekorg.tutortek

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.Request
import com.tutortekorg.tutortek.utils.JwtUtils
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.onboarding.MainActivity
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton

@SuppressLint("CustomSplashScreen")
class SplashscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setAppTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        performAutoLogin()
    }

    private fun setAppTheme() {
        val preferences = getSharedPreferences(TutortekConstants.SETTING_PREFERENCES, MODE_PRIVATE)
        val isDarkModeOn = preferences.getBoolean(TutortekConstants.DARK_MODE_FLAG, false)
        if(isDarkModeOn) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun performAutoLogin() {
        val token = JwtUtils.getJwtToken(this)
        if(token.isNullOrBlank()) navigateToOnboardingScreen()
        else sendAutoLoginRequest(token)
    }

    private fun sendAutoLoginRequest(token: String) {
        val profileId = JwtUtils.getProfileIdFromSavedToken(this)
        val url = "${TutortekConstants.BASE_URL}/profiles/$profileId"
        val request = TutortekObjectRequest(this, Request.Method.GET, url, null,
            {
                JwtUtils.addUserProfileBundle(it, token)
                navigateToHomeScreen()
            },
            {
                if(!JwtUtils.wasResponseUnauthorized(it)){
                    Toast.makeText(this, R.string.error_profile_retrieval, Toast.LENGTH_SHORT).show()
                    navigateToOnboardingScreen()
                }
            }
        )
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun navigateToOnboardingScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }

    private fun navigateToHomeScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 2000)
    }
}
