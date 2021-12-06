package com.tutortekorg.tutortek.requests

import android.app.Activity
import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.tutortekorg.tutortek.utils.JwtUtils
import org.json.JSONObject

class TutortekObjectRequest(private val context: Context,
                            method: Int,
                            url: String,
                            body: JSONObject?,
                            listener: Response.Listener<JSONObject>,
                            errorListener: Response.ErrorListener)
    : JsonObjectRequest(method, url, body, listener, errorListener) {

    override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
        if(response?.statusCode == 204)
            return Response.success(JSONObject(), HttpHeaderParser.parseCacheHeaders(response))

        return super.parseNetworkResponse(response)
    }

    override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
        if(volleyError?.let { JwtUtils.wasResponseUnauthorized(it) } == true) {
            JwtUtils.sendRefreshRequest(context as Activity, false, this)
            val response = NetworkResponse(401, null, false, 1L, null)
            return VolleyError(response)
        }
        return super.parseNetworkError(volleyError)
    }

    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        val token = JwtUtils.getJwtToken(context)
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = "Bearer $token"
        return headers
    }
}
