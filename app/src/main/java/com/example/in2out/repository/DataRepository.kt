package com.example.in2out.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.in2out.db.AppDatabase
import com.example.in2out.db.NoteData
import com.example.in2out.service.NotificationReceiver
import java.util.Calendar
import javax.inject.Inject

class DataRepository @Inject constructor(private val database: AppDatabase) {

    suspend fun datastore(intime: String, outime: String) {
        val data = NoteData(intime = intime, outime = outime)
        database.GetTimeDao().insert(data)
    }

    suspend fun getData(): List<NoteData> {
        return database.GetTimeDao().getAllTodo()
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarm(context: Context) {
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.HOUR_OF_DAY, 8)
        calendar.add(Calendar.MINUTE, 30)

        val alarmIntent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun stopAlarm(context: Context) {
        val stopIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("stop", true)
        }
        context.sendBroadcast(stopIntent)
        Log.d("DataRepository", "Stop intent broadcast sent")
    }

}
