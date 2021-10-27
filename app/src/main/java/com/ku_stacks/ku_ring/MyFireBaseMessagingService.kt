package com.ku_stacks.ku_ring

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ku_stacks.ku_ring.ui.home.HomeActivity
import timber.log.Timber

class MyFireBaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String){
        Timber.e("refreshed token : $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data?.let { map ->
            Timber.e(map["title2"])
            val str = map["title3"]
            sendNotification(str, str)
        }

    }

    private fun sendNotification(title: String?, body: String?){
        val intent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channelId = "ku_stack_channel_id"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}