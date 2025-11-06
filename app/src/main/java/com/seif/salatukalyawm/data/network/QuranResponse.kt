// In: data/network/QuranResponse.kt
package com.seif.salatukalyawm.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- نموذج لطلب قائمة السور (الفهرس) ---
@Serializable
data class SurahListResponse(
    val data: List<SurahInfo>
)

@Serializable
data class SurahInfo(
    val number: Int,
    val name: String, // اسم السورة بالعربي
    val englishName: String,
    val revelationType: String, // "Meccan" or "Medinan"
    val numberOfAyahs: Int
)


// --- نموذج لطلب سورة كاملة ---
@Serializable
data class SurahFullResponse(
    val data: SurahData
)

@Serializable
data class SurahData(
    val number: Int,
    val name: String,
    @SerialName("ayahs") // اسم الحقل في الـ JSON هو "ayahs"
    val ayahs: List<Ayah>
)

@Serializable
data class Ayah(
    val numberInSurah: Int,
    val text: String
)
