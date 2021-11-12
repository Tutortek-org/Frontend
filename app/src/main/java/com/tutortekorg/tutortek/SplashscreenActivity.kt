package com.tutortekorg.tutortek

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
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
        val token = TutortekUtils.getJwtToken(this)
        if(token.isNullOrBlank()) navigateToOnboardingScreen()
        else sendAutoLoginRequest(token)
    }

    private fun sendAutoLoginRequest(token: String) {
        val url = "${TutortekConstants.BASE_URL}/autologin"
        val request = object : JsonObjectRequest(
            Method.POST, url, null,
            {
                navigateToHomeScreen()
            },
            {
                if(it.networkResponse == null || it.networkResponse?.statusCode == 401)
                    TutortekUtils.sendRefreshRequest(token, this, true)
                else navigateToOnboardingScreen()
            }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
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
