package com.seif.salatukalyawm.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes // <-- Import جديد
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.seif.salatukalyawm.R
import com.seif.salatukalyawm.data.AdhanScheduler
import com.seif.salatukalyawm.data.network.Timings

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PrayerTimesScreen(
    prayerTimesViewModel: PrayerTimesViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    val adhanScheduler = remember { AdhanScheduler(context) }
    var hasExactAlarmPermission by remember { mutableStateOf(adhanScheduler.canScheduleExactAlarms()) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasExactAlarmPermission = adhanScheduler.canScheduleExactAlarms()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )
    val uiState by prayerTimesViewModel.prayerUiState

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted && activity != null) {
            try {
                LocationServices.getFusedLocationProviderClient(activity).lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            prayerTimesViewModel.fetchPrayerTimes(location)
                        }
                    }
            } catch (e: SecurityException) {
                // Handle exception
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!locationPermissionsState.allPermissionsGranted) {
            PermissionRequestContent(
                permissionText = stringResource(id = R.string.msg_location_permission_required),
                buttonText = stringResource(id = R.string.btn_grant_permission)
            ) {
                locationPermissionsState.launchMultiplePermissionRequest()
            }
        } else if (!hasExactAlarmPermission) {
            PermissionRequestContent(
                permissionText = stringResource(id = R.string.msg_exact_alarm_permission_required),
                buttonText = stringResource(id = R.string.btn_go_to_settings)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                }
            }
        } else {
            when (val state = uiState) {
                is PrayerUiState.Loading -> {
                    CircularProgressIndicator()
                    Text(stringResource(id = R.string.msg_loading_prayer_times), modifier = Modifier.padding(top = 16.dp))
                }
                is PrayerUiState.Success -> {
                    PrayerTimesContent(timings = state.timings, date = state.date)
                }
                is PrayerUiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun PermissionRequestContent(
    permissionText: String,
    buttonText: String,
    onRequest: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = permissionText,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onRequest) {
            Text(buttonText)
        }
    }
}

// --- EDITED SECTION START ---

@Composable
fun PrayerTimesContent(timings: Timings, date: String) {
    // The list now contains the English keys which we will use for mapping.
    val prayerList = listOf(
        "Fajr" to timings.Fajr,
        "Sunrise" to timings.Sunrise,
        "Dhuhr" to timings.Dhuhr,
        "Asr" to timings.Asr,
        "Maghrib" to timings.Maghrib,
        "Isha" to timings.Isha
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = date,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(prayerList) { (nameKey, time) ->
                // Pass the English key to the row composable.
                PrayerTimeRow(prayerNameKey = nameKey, prayerTime = time)
            }
        }
    }
}

@Composable
fun PrayerTimeRow(prayerNameKey: String, prayerTime: String) {
    // This helper function finds the correct string resource ID.
    val prayerNameResId = getPrayerNameResId(prayerNameKey)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Use stringResource() to display the translated name.
            Text(
                text = stringResource(id = prayerNameResId),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = prayerTime,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// This new helper function maps the English key to the correct String Resource ID.
@StringRes
private fun getPrayerNameResId(prayerNameKey: String): Int {
    return when (prayerNameKey.lowercase()) {
        "fajr" -> R.string.fajr
        "dhuhr" -> R.string.dhuhr
        "asr" -> R.string.asr
        "maghrib" -> R.string.maghrib
        "isha" -> R.string.isha
        "sunrise" -> R.string.sunrise
        else -> R.string.app_name // Fallback in case of an unknown key
    }
}

// --- EDITED SECTION END ---
