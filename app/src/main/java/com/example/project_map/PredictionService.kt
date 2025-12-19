package com.example.project_map

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class PredictionService : Service() {

    companion object {
        const val CHANNEL_ID = "prediction_channel"
        const val FOREGROUND_ID = 101
        const val FINISH_ID = 102
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val notification: Notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Prediksi berjalan")
                .setContentText("Sedang memproses data...")
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

        startForeground(FOREGROUND_ID, notification)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Prediksi Penjualan",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi proses dan hasil prediksi"
            }

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showFinishedNotification(context: Context) {
        val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Prediksi selesai")
                .setContentText("Hasil prediksi berhasil dibuat")
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(FINISH_ID, notification)
    }
}
