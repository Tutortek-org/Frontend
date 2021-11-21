package com.tutortekorg.tutortek

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.android.volley.Request
import com.android.volley.VolleyError
import com.auth0.android.jwt.JWT
import com.tutortekorg.tutortek.authentication.LoginActivity
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONObject

class JwtUtils {
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

        fun sendRefreshRequest(activity: Activity, shouldNavigateToHome: Boolean) {
            val url = "${TutortekConstants.BASE_URL}/refresh"
            val request = TutortekRequest(activity, Request.Method.GET, url, null,
                {
                    if(shouldNavigateToHome) navigateToHomeScreen(activity)
                },
                {
                    navigateToLoginScreen(activity)
                }
            )
            RequestSingleton.getInstance(activity).addToRequestQueue(request)
        }

        fun getEmailFromSavedToken(context: Context): String? {
            val token = getJwtToken(context)
            val jwt = token?.let { JWT(it) }
            val email = jwt?.getClaim("sub")
            return email?.asString()
        }

        fun getProfileIdFromSavedToken(context: Context): Long? {
            val token = getJwtToken(context)
            val jwt = token?.let { JWT(it) }
            val profileId = jwt?.getClaim("pid")
            return profileId?.asLong()
        }

        fun wasResponseUnauthorized(error: VolleyError): Boolean =
            error.networkResponse == null || error.networkResponse.statusCode == 401

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
