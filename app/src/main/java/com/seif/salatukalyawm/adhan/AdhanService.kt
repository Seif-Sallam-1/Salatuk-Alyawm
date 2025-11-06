// In: adhan/AdhanService.kt
package com.seif.salatukalyawm.adhan

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.seif.salatukalyawm.MainActivity
import com.seif.salatukalyawm.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build

class AdhanService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "AdhanChannel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("AdhanCheck", "Checkpoint 4: AdhanService has started.")

        val prayerName = intent?.getStringExtra("PRAYER_NAME") ?: "الصلاة"

        createNotificationChannel()

        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("حان الآن وقت صلاة $prayerName")
            .setContentText("اضغط هنا لفتح التطبيق")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // تأكد من وجود هذه الأيقونة
            .setContentIntent(pendingIntent)
            .build()

        try {
            startForeground(NOTIFICATION_ID, notification)
            Log.d("AdhanCheck", "startForeground was called successfully.")
        } catch (e: Exception) {
            Log.e("AdhanCheck", "Failed to call startForeground.", e)
            stopSelf()
            return START_NOT_STICKY
        }

        Log.d("AdhanCheck", "Attempting to play sound.")
        mediaPlayer = MediaPlayer.create(this, R.raw.adhan)
        if (mediaPlayer == null) {
            Log.e("AdhanCheck", "MediaPlayer creation failed! Check R.raw.adhan.")
            stopSelf()
            return START_NOT_STICKY
        }

        mediaPlayer?.apply {
            setOnCompletionListener {
                Log.d("AdhanCheck", "MediaPlayer completed. Stopping service and removing notification.")
                stopForeground(STOP_FOREGROUND_REMOVE) // الطريقة الصحيحة لإيقاف الخدمة وإزالة الإشعار
                stopSelf() // كإجراء إضافي لضمان توقف الخدمة
            }
            setOnErrorListener { _, _, _ ->
                Log.e("AdhanCheck", "MediaPlayer error occurred.")
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                true
            }
            start()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("AdhanCheck", "AdhanService destroyed.")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Adhan Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Adhan prayer time notifications"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
