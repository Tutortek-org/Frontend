package com.tutortekorg.tutortek.services

import android.util.Log
import com.android.volley.Request
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONObject

class TutortekMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val url = "${TutortekConstants.BASE_URL}/notifications"
        val body = JSONObject()
        body.put("deviceToken", token)
        val request = TutortekObjectRequest(applicationContext, Request.Method.POST, url, body, {}, {})
        RequestSingleton.getInstance(applicationContext).addToRequestQueue(request)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}
