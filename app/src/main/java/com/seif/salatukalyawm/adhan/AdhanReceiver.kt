// In: adhan/AdhanReceiver.kt
package com.seif.salatukalyawm.adhan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AdhanReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // --- نقطة التفتيش 2: هل استيقظ الـ Receiver؟ ---
        Log.e("AdhanCheck", "Checkpoint 2: AdhanReceiver woke up!")

        val prayerName = intent.getStringExtra("PRAYER_NAME") ?: "الصلاة"

        val serviceIntent = Intent(context, AdhanService::class.java).apply {
            putExtra("PRAYER_NAME", prayerName)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            // --- نقطة التفتيش 3: هل تم إرسال أمر تشغيل الخدمة؟ ---
            Log.e("AdhanCheck", "Checkpoint 3: startForegroundService command sent for $prayerName.")
        } catch (e: Exception) {
            Log.e("AdhanCheck", "Checkpoint 3 FAILED: Could not start service.", e)
        }
    }
}
