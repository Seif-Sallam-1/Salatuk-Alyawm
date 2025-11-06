// In: data/QuranTextData.kt
package com.seif.salatukalyawm.data

import kotlinx.serialization.Serializable

// This class now perfectly matches the structure of your mainDataQuran.json file.
@Serializable
data class SurahText(
    val number: Int,
    val name: Name,
    val verses: List<Verse>
) {
    // Helper property to get the starting page number of the surah.
    val startPage: Int
        get() = verses.firstOrNull()?.page ?: 0
}

@Serializable
data class Name(
    val ar: String,
    val en: String,
    val transliteration: String
)

// --- THIS IS THE FIX ---
// I have removed the 'juz' field that was causing the crash.
@Serializable
data class Verse(
    val number: Int,
    val text: Text,
    val page: Int
)

@Serializable
data class Text(
    val ar: String,
    val en: String
)

// This data class is NOT based on a JSON file.
// It's a simple data holder we use inside the ViewModel.
data class QuranPageInfo(
    val pageNumber: Int,
    val surahName: String,
    val juzNumber: Int
)
