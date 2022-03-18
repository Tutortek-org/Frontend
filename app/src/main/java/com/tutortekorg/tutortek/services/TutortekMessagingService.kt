package com.tutortekorg.tutortek.services

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.Request
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.SplashscreenActivity
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
        val json = JSONObject(message.data["default"]!!)
        val title = json.getString("title")
        val content = json.getString("content")
        buildTheNotification(title, content)
    }

    private fun buildTheNotification(title: String, content: String) {
        val intent = Intent(this, SplashscreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, TutortekConstants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
