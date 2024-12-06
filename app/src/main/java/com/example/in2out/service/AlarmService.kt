package com.example.in2out.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.in2out.MainActivity


class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            startForegroundWithPlaceholderNotification()
        }

        when (intent?.action) {
            IntentAction.START.toString() -> {
                startServiceAction()
            }
            IntentAction.STOP.toString() -> {
                stopServiceAction()
            }
        }
        return START_STICKY
    }

    private fun startServiceAction() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI).apply {
                isLooping = true
                start()
            }

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("screen", "AlarmScreen")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alarm Triggered")
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // Ensure this icon is valid
                .setContentText("Time is up! Tap to stop the alarm.")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()

            startForeground(NOTIFICATION_ID, notification)
        } else {
            mediaPlayer?.start()
        }
    }

    private fun stopServiceAction() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alarm notifications"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun startForegroundWithPlaceholderNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Service Starting...")
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    enum class IntentAction {
        START, STOP
    }

    companion object {
        private const val CHANNEL_ID = "alarm_channel_id"
        private const val NOTIFICATION_ID = 1001
    }
}
