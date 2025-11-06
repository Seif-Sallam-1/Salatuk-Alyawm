// In: data/sensors/QiblaSensorManager.kt
package com.seif.salatukalyawm.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class QiblaSensorManager(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationVectorSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    // Flow لبث بيانات زاوية دوران الهاتف
    val rotationAngle: Flow<Float> = callbackFlow {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                        val rotationMatrix = FloatArray(9)
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
                        val orientationAngles = FloatArray(3)
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)

                        // الزاوية التي تهمنا هي زاوية السمت (Azimuth) حول المحور Z
                        // وهي بالراديان، لذا نحولها إلى درجات
                        val azimuthInRadians = orientationAngles[0]
                        val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()

                        // نرسل القيمة الجديدة إلى الـ Flow
                        trySend(azimuthInDegrees)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // يمكن التعامل معها لاحقًا إذا أردنا
            }
        }

        // تسجيل الـ listener عند بدء جمع البيانات من الـ Flow
        sensorManager.registerListener(sensorListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)

        // إلغاء تسجيل الـ listener عند إيقاف جمع البيانات
        awaitClose {
            sensorManager.unregisterListener(sensorListener)
        }
    }
}
