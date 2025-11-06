// In: data/network/RetrofitInstance.kt
package com.seif.salatukalyawm.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import kotlin.getValue


object RetrofitInstance {

    private val json = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType( )

    // --- نسخة Retrofit الخاصة بـ Aladhan API ---
    val aladhanApi: AladhanApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://api.aladhan.com/" ) // <-- الرابط الأصلي لمواقيت الصلاة
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(AladhanApiService::class.java)
    }

    // --- نسخة Retrofit الخاصة بـ Quran API ---
    val quranApi: QuranApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.alquran.cloud/" ) // <-- رابط القرآن
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(QuranApiService::class.java)
    }
}
