// In: ui/QiblaViewModel.kt
package com.seif.salatukalyawm.ui

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import com.seif.salatukalyawm.data.sensors.QiblaSensorManager
import kotlinx.coroutines.flow.Flow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class QiblaViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorManager = QiblaSensorManager(application)

    // بث بيانات دوران الهاتف مباشرة من مدير المستشعرات
    val deviceRotation: Flow<Float> = sensorManager.rotationAngle

    // إحداثيات الكعبة المشرفة
    private val kaabaLatitude = 21.422487
    private val kaabaLongitude = 39.826206

    /**
     * دالة لحساب زاوية القبلة بناءً على موقع المستخدم
     * @param userLocation موقع المستخدم الحالي
     * @return زاوية القبلة بالدرجات
     */
    fun calculateQiblaAngle(userLocation: Location): Float {
        val userLat = Math.toRadians(userLocation.latitude)
        val userLon = Math.toRadians(userLocation.longitude)
        val kaabaLat = Math.toRadians(kaabaLatitude)
        val kaabaLon = Math.toRadians(kaabaLongitude)

        val deltaLon = kaabaLon - userLon

        val y = sin(deltaLon) * cos(kaabaLat)
        val x = cos(userLat) * sin(kaabaLat) - sin(userLat) * cos(kaabaLat) * cos(deltaLon)

        val angleInRadians = atan2(y, x)

        // تحويل الزاوية من راديان إلى درجات
        return Math.toDegrees(angleInRadians).toFloat()
    }
}
