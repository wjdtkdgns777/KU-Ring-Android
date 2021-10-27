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
import com.ku_stacks.ku_ring.data.db.PushDao
import com.ku_stacks.ku_ring.data.db.PushEntity
import com.ku_stacks.ku_ring.di.DBModule.provideKuRingDatabase
import com.ku_stacks.ku_ring.di.DBModule.providePushDao
import com.ku_stacks.ku_ring.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MyFireBaseMessagingService : FirebaseMessagingService() {

    private var pushDao: PushDao? = null

    override fun onNewToken(token: String){
        Timber.e("refreshed token : $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        readyForDatabase()

        val articleId = remoteMessage.data["articleId"]
        val category = remoteMessage.data["category"]
        val postedDate = remoteMessage.data["postedDate"]
        val subject = remoteMessage.data["subject"]
        val baseUrl = remoteMessage.data["baseUrl"]

        if (articleId.isNullOrEmpty() || category.isNullOrEmpty() || postedDate.isNullOrEmpty()
            || subject.isNullOrEmpty() || baseUrl.isNullOrEmpty()) {
            return
        }

        insertNotificationIntoDatabase(articleId, category, postedDate, subject, baseUrl)
        sendNotification(title = subject, body = category)
    }

    private fun readyForDatabase() {
        pushDao = providePushDao(provideKuRingDatabase(applicationContext))
    }

    private fun insertNotificationIntoDatabase(
        articleId: String,
        category: String,
        postedDate: String,
        subject: String,
        baseUrl: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                pushDao?.insertNotification(
                    PushEntity(
                        articleId = articleId,
                        category = category,
                        postedDate = postedDate,
                        subject = subject,
                        baseUrl = baseUrl,
                        isNew = true
                    )
                )
                Timber.e("insert notification success")
            }
            catch (e: Exception) {
                Timber.e("insert notification error : $e")
            }

        }
    }

    private fun sendNotification(title: String?, body: String?){
        val intent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channelId = "알림 소리"
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