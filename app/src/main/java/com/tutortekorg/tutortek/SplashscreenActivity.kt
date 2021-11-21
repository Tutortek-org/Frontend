package com.tutortekorg.tutortek

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.android.volley.Request
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.onboarding.MainActivity
import com.tutortekorg.tutortek.singletons.RequestSingleton

@SuppressLint("CustomSplashScreen")
class SplashscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        performAutoLogin()
    }

    private fun performAutoLogin() {
        val token = JwtUtils.getJwtToken(this)
        if(token.isNullOrBlank()) navigateToOnboardingScreen()
        else sendAutoLoginRequest()
    }

    private fun sendAutoLoginRequest() {
        val url = "${TutortekConstants.BASE_URL}/autologin"
        val request = TutortekRequest(this, Request.Method.POST, url, null,
            {
                navigateToHomeScreen()
            },
            {
                if(JwtUtils.wasResponseUnauthorized(it))
                    JwtUtils.sendRefreshRequest(this, true)
                else navigateToOnboardingScreen()
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
