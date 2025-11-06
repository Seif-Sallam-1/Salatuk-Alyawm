// In: data/AdhanScheduler.kt
package com.seif.salatukalyawm.data // Or your package

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.seif.salatukalyawm.adhan.AdhanReceiver
import java.util.Calendar

class AdhanScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true // الإصدارات الأقدم لا تحتاج لهذا الإذن
        }
    }

    fun schedule(prayerTime: Calendar, prayerName: String) {
        if (!canScheduleExactAlarms()) {
            Log.e("AdhanScheduler", "Cannot schedule exact alarms. Permission not granted.")
            return // توقف هنا إذا لم يكن الإذن ممنوحًا
        }

        val intent = Intent(context, AdhanReceiver::class.java).apply {
            putExtra("PRAYER_NAME", prayerName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                prayerTime.timeInMillis,
                pendingIntent
            )
            // استخدمنا AdhanCheck كنقطة تفتيش
            Log.d("AdhanCheck", "Checkpoint 1: Successfully scheduled $prayerName for ${prayerTime.time}")
        } catch (e: SecurityException) {
            Log.e("AdhanCheck", "SecurityException on schedule for $prayerName.", e)
        }
    }

    fun cancel(prayerName: String) {
        val intent = Intent(context, AdhanReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
