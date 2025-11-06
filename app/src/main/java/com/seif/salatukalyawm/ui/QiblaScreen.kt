// In: ui/QiblaScreen.kt
package com.seif.salatukalyawm.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.seif.salatukalyawm.R // تأكد من عمل import

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QiblaScreen() {
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // اطلب الإذن عند فتح الشاشة لأول مرة
    LaunchedEffect(Unit) {
        if (!locationPermissions.allPermissionsGranted) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }

    when {
        locationPermissions.allPermissionsGranted -> {
            // الأذونات ممنوحة، اعرض محتوى البوصلة
            QiblaCompassContent()
        }
        else -> {
            // --- هذا هو التعديل ---
            // الأذونات مرفوضة، اعرض شاشة طلب الإذن ومرر لها الحالة
            PermissionDeniedContent(locationPermissions = locationPermissions)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionDeniedContent(locationPermissions: MultiplePermissionsState) {
    TODO("Not yet implemented")
}

@SuppressLint("MissingPermission")
@Composable
fun QiblaCompassContent(qiblaViewModel: QiblaViewModel = viewModel()) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<Location?>(null) }

    // 1. جلب الموقع
    LaunchedEffect(Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            userLocation = location
        }
    }

    // 2. عرض المحتوى بناءً على حالة الموقع
    if (userLocation == null) {
        // إذا لم يتم تحديد الموقع بعد
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("جاري تحديد موقعك...", fontSize = 18.sp)
            }
        }
    } else {
        // إذا تم تحديد الموقع، اعرض البوصلة
        val deviceRotation by qiblaViewModel.deviceRotation.collectAsState(initial = 0f)
        val qiblaAngle = qiblaViewModel.calculateQiblaAngle(userLocation!!)
        val needleRotation = qiblaAngle - deviceRotation

        // 3. منطق الاهتزاز
        val isDirectionCorrect = needleRotation in -2f..2f // إذا كانت الزاوية بين -2 و 2 درجة
        if (isDirectionCorrect) {
            Vibrate(context)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.compass_background),
                contentDescription = "Compass Background",
                modifier = Modifier.fillMaxSize(0.9f)
            )
            Image(
                painter = painterResource(id = R.drawable.compass_needle),
                contentDescription = "Compass Needle",
                modifier = Modifier
                    .size(180.dp)
                    .rotate(needleRotation)
            )
        }
    }
}

// دالة مساعدة للاهتزاز
@Composable
private fun Vibrate(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    // اهتزازة قصيرة
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }
}
