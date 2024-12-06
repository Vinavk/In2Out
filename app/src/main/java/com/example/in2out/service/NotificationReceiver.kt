package com.example.in2out.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { ctx ->
            val actionIntent = Intent(ctx, AlarmService::class.java)

            val isStopAction = intent?.getBooleanExtra("stop", false) ?: false
            actionIntent.action = if (isStopAction) {
                AlarmService.IntentAction.STOP.toString()
            } else {
                AlarmService.IntentAction.START.toString()
            }
            Log.d("NotificationReceiver", "Received action: $isStopAction")
            ContextCompat.startForegroundService(ctx, actionIntent)
        }
    }
}
