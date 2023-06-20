package com.example.theproj

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class AlarmService : Service() {

    private val CHANNEL_ID = "testChannel01"   // Channel for notification
    private var notificationManager: NotificationManager? = null
    private lateinit var timer : Timer

    override fun onBind(intent: Intent?): IBinder? {

        return onBind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Log", "Service Start")
        createNotificationChannel(CHANNEL_ID, "testChannel01", NotificationManager.IMPORTANCE_HIGH)

        timer(period = 1000) {
            Log.d("time_log", getTime().toString())
            if(getTime().toString() == "07:00:00") {
                displayNotification()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("Log", "Service Stop")
        super.onDestroy()
    }

    private fun createNotificationChannel(channelId: String, name: String, importance: Int) {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
           notificationManager!!.createNotificationChannel(NotificationChannel(channelId, name, importance))
        }
    }

    private fun displayNotification() {
        val notificationId = 66

        val notification = Notification.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_park_24)
            .setContentTitle("안녕하세요!")
            .setContentText("오늘은 산책을 가볼까요?")
            .build()

        notificationManager?.notify(notificationId, notification)
    }

    private fun getTime(): String? {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val dateFormat = SimpleDateFormat("hh:mm:ss")
        var getTime = dateFormat.format(date)

        return getTime
    }
}