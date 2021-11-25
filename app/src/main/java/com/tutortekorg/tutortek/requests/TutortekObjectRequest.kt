package com.tutortekorg.tutortek.requests

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.authentication.JwtUtils
import org.json.JSONObject

class TutortekObjectRequest(private val context: Context,
                            method: Int,
                            url: String,
                            body: JSONObject?,
                            listener: Response.Listener<JSONObject>,
                            errorListener: Response.ErrorListener)
    : JsonObjectRequest(method, url, body, listener, errorListener) {

    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        val token = JwtUtils.getJwtToken(context)
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = "Bearer $token"
        return headers
    }
}
