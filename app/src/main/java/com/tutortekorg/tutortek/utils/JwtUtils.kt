package com.tutortekorg.tutortek.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.android.volley.Request
import com.android.volley.VolleyError
import com.auth0.android.jwt.JWT
import com.tutortekorg.tutortek.HomeActivity
import com.tutortekorg.tutortek.authentication.LoginActivity
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.UserProfile
import com.tutortekorg.tutortek.singletons.ProfileSingleton
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONObject

object JwtUtils {
    fun saveJwtToken(context: Context, response: JSONObject) {
        val preferences =
            context.getSharedPreferences(TutortekConstants.AUTH_PREFERENCES, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val token = response.getString(TutortekConstants.TOKEN_KEY)
        editor.putString(TutortekConstants.TOKEN_KEY, token)
        editor.apply()
    }

    fun invalidateJwtToken(context: Context) {
        val preferences =
            context.getSharedPreferences(TutortekConstants.AUTH_PREFERENCES, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove(TutortekConstants.TOKEN_KEY)
        editor.apply()
    }

    fun getJwtToken(context: Context) =
        context.getSharedPreferences(TutortekConstants.AUTH_PREFERENCES, Context.MODE_PRIVATE)
            .getString(TutortekConstants.TOKEN_KEY, "")

    fun <T> sendRefreshRequest(
        activity: Activity,
        shouldNavigateToHome: Boolean,
        requestToRepeat: Request<T>?
    ) {
        val url = "${TutortekConstants.BASE_URL}/refresh"
        val request = TutortekObjectRequest(activity, Request.Method.GET, url, null,
            {
                saveJwtToken(activity, it)
                if (requestToRepeat != null)
                    RequestSingleton.getInstance(activity).addToRequestQueue(requestToRepeat)
                if (shouldNavigateToHome) navigateToHomeScreen(activity)
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

    fun getUserIdFromSavedToken(context: Context): Long? {
        val token = getJwtToken(context)
        val jwt = token?.let { JWT(it) }
        val email = jwt?.getClaim("uid")
        return email?.asLong()
    }

    fun getProfileIdFromSavedToken(context: Context): Long? {
        val token = getJwtToken(context)
        val jwt = token?.let { JWT(it) }
        val profileId = jwt?.getClaim("pid")
        return profileId?.asLong()
    }

    fun addUserProfileBundle(body: JSONObject, token: String) {
        val roles = getRoles(token)
        val profile = UserProfile(body, roles)
        ProfileSingleton.getInstance().userProfile = profile
    }

    private fun getRoles(token: String): List<String> {
        val jwt = JWT(token)
        val roles = jwt.getClaim("roles")
        return roles.asList(String::class.java)
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
