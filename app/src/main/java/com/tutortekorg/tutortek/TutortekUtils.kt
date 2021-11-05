package com.tutortekorg.tutortek

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.authentication.LoginActivity
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.onboarding.MainActivity
import org.json.JSONObject

class TutortekUtils {
    companion object {
        fun saveJwtToken(context: Context, response: JSONObject) {
            val preferences = context.getSharedPreferences(TutortekConstants.AUTH_PREFERENCES, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            val token = response.getString(TutortekConstants.TOKEN_KEY)
            editor.putString(TutortekConstants.TOKEN_KEY, token)
            editor.apply()
        }

        fun invalidateJwtToken(context: Context) {
            val preferences = context.getSharedPreferences(TutortekConstants.AUTH_PREFERENCES, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.remove(TutortekConstants.TOKEN_KEY)
            editor.apply()
        }

        fun getJwtToken(context: Context) =
            context.getSharedPreferences(TutortekConstants.AUTH_PREFERENCES, Context.MODE_PRIVATE)
                .getString(TutortekConstants.TOKEN_KEY, "")

        fun sendRefreshRequest(token: String, activity: Activity, shouldNavigateToHome: Boolean) {
            val url = "${TutortekConstants.BASE_URL}/refresh"
            val request = object : JsonObjectRequest(
                Method.GET, url, null,
                {
                    navigateToHomeScreen(activity)
                },
                {
                    navigateToLoginScreen(activity)
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
            RequestSingleton.getInstance(activity).addToRequestQueue(request)
        }

        private fun navigateToLoginScreen(activity: Activity) {
            activity.startActivity(Intent(activity, LoginActivity::class.java))
            activity.finish()
        }

        private fun navigateToHomeScreen(activity: Activity) {
            activity.startActivity(Intent(activity, HomeActivity::class.java))
            activity.finish()
        }
    }
}
