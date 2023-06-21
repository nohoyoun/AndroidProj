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
import android.widget.Toast
import com.example.theproj.retrofit.AirQualityResponse
import com.example.theproj.retrofit.AirQualityService
import com.example.theproj.retrofit.RetrofitConnection
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
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

        timer(period = 1000*60*60) {//1시간 마다 갱신
            getAirQualityData(37.3306890, 126.5930664)      //서울특별시 위도경도
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun getAirQualityData(latitude: Double, longitude: Double) {
        val retrofitAPI = RetrofitConnection.getInstance().create(AirQualityService::class.java)
        retrofitAPI.getAirQualityData(
            latitude.toString(), longitude.toString(), "f9294b64-cf60-497e-a9ba-2ec722890df6"
        ).enqueue(object : Callback<AirQualityResponse> {
            override fun onResponse(
                call: Call<AirQualityResponse>, response: Response<AirQualityResponse>
            ) {

                if (response.isSuccessful) {
                    response.body()?.let { updateWP(it) }
                } else {
                Log.d("업데이트", "실패")
                }
            }

            override fun onFailure(call: Call<AirQualityResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun updateWP(airQualityData: AirQualityResponse) {
        val weatherData = airQualityData.data.current.weather
        Log.d("온도", weatherData.tp.toString())
        val pollutionData = airQualityData.data.current.pollution
        Log.d("미먼", pollutionData.aqius.toString())

        val dateTime = ZonedDateTime.parse(weatherData.ts).withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime()

        if(pollutionData.aqius <= 50){
            displayNotification(weatherData.ic)

        }

    }

    override fun onDestroy() {
        Log.d("Log", "Service Stop")
        //
        super.onDestroy()
    }

    private fun createNotificationChannel(channelId: String, name: String, importance: Int) {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
           notificationManager!!.createNotificationChannel(NotificationChannel(channelId, name, importance))
        }
    }

    private fun displayNotification(id : String) {
        val notificationId = 66

        if(id == "01d") {
            val notification = Notification.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_park_24)
                .setContentTitle("안녕하세요!")
                .setContentText("날이 개었으니 산책을 가볼까요?")
                .build()

            notificationManager?.notify(notificationId, notification)
                }else{

            val notification = Notification.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_park_24)
                .setContentTitle("안녕하세요!")
                .setContentText("날씨가 조금 흐리지만 이 정도면 괜찮아요! 산책을 가볼까요?")
                .build()

            notificationManager?.notify(notificationId, notification)
                }

    }

}