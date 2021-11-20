package com.tutortekorg.tutortek

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class TutortekRequest(private val context: Context,
                      method: Int,
                      url: String,
                      body: JSONObject?,
                      listener: Response.Listener<JSONObject>,
                      errorListener: Response.ErrorListener)
    : JsonObjectRequest(method, url, body, listener, errorListener) {

    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        val token = TutortekUtils.getJwtToken(context)
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = "Bearer $token"
        return headers
    }
}
