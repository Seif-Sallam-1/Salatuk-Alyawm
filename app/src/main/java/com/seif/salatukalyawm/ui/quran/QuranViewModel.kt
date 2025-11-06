// In: ui/quran/QuranViewModel.kt
package com.seif.salatukalyawm.ui.quran

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.seif.salatukalyawm.data.QuranPageInfo
import com.seif.salatukalyawm.data.SurahText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed interface QuranIndexUiState {
    object Loading : QuranIndexUiState
    data class Success(val surahs: List<SurahText>) : QuranIndexUiState
    data class Error(val message: String) : QuranIndexUiState
}

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<QuranIndexUiState>(QuranIndexUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // This will hold the footer info for all 604 pages.
    private val pageInfoMap = mutableMapOf<Int, QuranPageInfo>()

    init {
        loadQuranData()
    }

    private fun loadQuranData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonString = getApplication<Application>().assets.open("mainDataQuran.json")
                    .bufferedReader().use { it.readText() }

                val json = Json { ignoreUnknownKeys = true }
                val surahs = json.decodeFromString<List<SurahText>>(jsonString)

                if (surahs.isNotEmpty()) {
                    // --- THIS IS THE CRITICAL FIX ---
                    // We process the data to ensure it's sorted correctly before using it.
                    val processedSurahs = surahs.map { surah ->
                        // Sort the verses by their overall number to guarantee correct order.
                        val sortedVerses = surah.verses.sortedBy { it.number }
                        surah.copy(verses = sortedVerses)
                    }

                    // Now, build the footer data using the safe, sorted data.
                    buildPageInfo(processedSurahs)
                    _uiState.value = QuranIndexUiState.Success(processedSurahs)
                    Log.d("QuranViewModel", "Successfully loaded and PROCESSED Quran data.")
                } else {
                    _uiState.value = QuranIndexUiState.Error("Quran data file is empty.")
                }

            } catch (e: Exception) {
                val errorMessage = "Failed to load or parse Quran data: ${e.message}"
                _uiState.value = QuranIndexUiState.Error(errorMessage)
                Log.e("QuranViewModel", errorMessage, e)
            }
        }
    }

    // This function builds the footer data. It is now correct.
    private fun buildPageInfo(surahs: List<SurahText>) {
        val juzStartPages = mapOf(
            1 to 1, 2 to 22, 3 to 42, 4 to 62, 5 to 82, 6 to 102, 7 to 122, 8 to 142, 9 to 162,
            10 to 182, 11 to 202, 12 to 222, 13 to 242, 14 to 262, 15 to 282, 16 to 302, 17 to 322,
            18 to 342, 19 to 362, 20 to 382, 21 to 402, 22 to 422, 23 to 442, 24 to 462, 25 to 482,
            26 to 502, 27 to 522, 28 to 542, 29 to 562, 30 to 582
        )

        for (pageNumber in 1..604) {
            // Find the last surah that starts on or before this page.
            val surahOnPage = surahs.lastOrNull { it.startPage <= pageNumber }
            val surahName = surahOnPage?.name?.ar ?: ""

            // Find the last juz that starts on or before this page.
            val juzOnPage = juzStartPages.entries.lastOrNull { it.value <= pageNumber }?.key ?: 1

            pageInfoMap[pageNumber] = QuranPageInfo(pageNumber, surahName, juzOnPage)
        }
        Log.d("QuranViewModel", "Successfully built page info for 604 pages.")
    }

    // This function is used by the QuranReaderScreen's footer.
    fun getPageInfo(pageNumber: Int): QuranPageInfo? {
        return pageInfoMap[pageNumber]
    }

    // Helper function to make .copy() work on the data class
    private fun SurahText.copy(verses: List<com.seif.salatukalyawm.data.Verse>): SurahText {
        return SurahText(
            number = this.number,
            name = this.name,
            verses = verses
        )
    }
}
