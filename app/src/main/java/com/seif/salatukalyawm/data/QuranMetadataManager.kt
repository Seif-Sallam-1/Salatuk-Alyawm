// In: data/QuranMetadataManager.kt
package com.seif.salatukalyawm.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

@Serializable
data class PageMetadata(
    val page: Int,
    val surah_number: Int,
    val surah_name_ar: String,
    val juz_number: Int
)

class QuranMetadataManager(private val context: Context) {

    private val metadata: List<PageMetadata> by lazy {
        loadMetadata()
    }

    private fun loadMetadata(): List<PageMetadata> {
        return try {
            val jsonString = context.assets.open("quran_metadata.json").bufferedReader().use { it.readText() }
            val loadedData = Json { ignoreUnknownKeys = true }.decodeFromString<List<PageMetadata>>(jsonString)

            // --- 2. هذا هو السطر الأهم ---
            Log.d("QuranMetadata", "Successfully loaded ${loadedData.size} pages from JSON.")

            loadedData
        } catch (e: IOException) {
            // --- 3. وهذا السطر مهم أيضًا ---
            Log.e("QuranMetadata", "ERROR: Failed to load or parse quran_metadata.json", e)

            e.printStackTrace()
            emptyList()
        }
    }

    fun getMetadataForPage(pageNumber: Int): PageMetadata? {
        // Log.d("QuranMetadata", "Searching for page: $pageNumber") // يمكنك تفعيل هذا للتحقق من الأرقام
        return metadata.find { it.page == pageNumber }
    }
}
