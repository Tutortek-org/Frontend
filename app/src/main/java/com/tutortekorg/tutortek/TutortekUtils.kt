package com.tutortekorg.tutortek

import android.content.Context
import com.tutortekorg.tutortek.constants.TutortekConstants
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
    }
}
