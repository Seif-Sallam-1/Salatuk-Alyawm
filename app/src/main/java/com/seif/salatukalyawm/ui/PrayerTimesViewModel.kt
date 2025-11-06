// In: ui/PrayerTimesViewModel.kt
package com.seif.salatukalyawm.ui

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.seif.salatukalyawm.data.AdhanScheduler
import com.seif.salatukalyawm.data.SettingsManager
import com.seif.salatukalyawm.data.network.AladhanApiService
import com.seif.salatukalyawm.data.network.PrayerTimesResponse
import com.seif.salatukalyawm.data.network.RetrofitInstance
import com.seif.salatukalyawm.data.network.Timings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed interface PrayerUiState {
    object Loading : PrayerUiState
    data class Success(val timings: Timings, val date: String) : PrayerUiState
    data class Error(val message: String) : PrayerUiState
}

class PrayerTimesViewModel(application: Application) : AndroidViewModel(application) {

    private val _prayerUiState = mutableStateOf<PrayerUiState>(PrayerUiState.Loading)
    val prayerUiState: State<PrayerUiState> = _prayerUiState

    private val settingsManager = SettingsManager(application)
    private val apiService: AladhanApiService = RetrofitInstance.aladhanApi
    private val adhanScheduler = AdhanScheduler(application)

    fun fetchPrayerTimes(location: Location) {
        viewModelScope.launch {
            _prayerUiState.value = PrayerUiState.Loading
            try {
                val method = settingsManager.prayerMethodFlow.first()
                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val currentDate = sdf.format(Date())

                val response = apiService.getPrayerTimes(
                    date = currentDate,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    method = method
                )

                val data = response.data
                _prayerUiState.value = PrayerUiState.Success(data.timings, data.date.readable)

                // جدولة الأذان للأوقات الحقيقية
                scheduleAdhanAlarms(data.timings)

            } catch (e: Exception) {
                Log.e("PrayerTimesViewModel", "Error fetching prayer times", e)
                _prayerUiState.value = PrayerUiState.Error(e.message ?: "حدث خطأ أثناء جلب مواقيت الصلاة")
            }
        }
    }

    // --- هذا هو الكود الأصلي لجدولة الأوقات الحقيقية ---
    private fun scheduleAdhanAlarms(timings: Timings) {
        val prayerMap = mapOf(
            "Fajr" to timings.Fajr,
            "Dhuhr" to timings.Dhuhr,
            "Asr" to timings.Asr,
            "Maghrib" to timings.Maghrib,
            "Isha" to timings.Isha
        )

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Calendar.getInstance()

        prayerMap.forEach { (prayerName, timeStr) ->
            try {
                val prayerTime = Calendar.getInstance().apply {
                    val parsed = sdf.parse(timeStr) ?: return@forEach
                    set(Calendar.HOUR_OF_DAY, parsed.hours)
                    set(Calendar.MINUTE, parsed.minutes)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                if (prayerTime.before(now)) {
                    prayerTime.add(Calendar.DATE, 1)
                }

                adhanScheduler.schedule(prayerTime, prayerName)

            } catch (e: Exception) {
                Log.e("AdhanScheduler", "Failed to schedule $prayerName", e)
            }
        }
    }
}
