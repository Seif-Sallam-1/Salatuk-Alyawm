// In: data/network/PrayerTimesResponse.kt
package com.seif.salatukalyawm.data.network // Or your package name

import kotlinx.serialization.Serializable

@Serializable
data class PrayerTimesResponse(
    val data: Data
)

@Serializable
data class Data(
    val timings: Timings,
    val date: DateInfo // <-- هذا هو الحقل الجديد والمهم
)

@Serializable
data class Timings(
    val Fajr: String,
    val Sunrise: String,
    val Dhuhr: String,
    val Asr: String,
    val Maghrib: String,
    val Isha: String
)

// --- هذه هي الفئة الجديدة التي أضفناها ---
@Serializable
data class DateInfo(
    val readable: String, // e.g., "05 Nov 2025"
    val gregorian: GregorianDate
)

@Serializable
data class GregorianDate(
    val date: String // e.g., "05-11-2025"
)
