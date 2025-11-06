// In: data/network/QuranApiService.kt
package com.seif.salatukalyawm.data.network

import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {
    @GET("v1/surah" )
    suspend fun getSurahList(): SurahListResponse

    @GET("v1/surah/{number}")
    suspend fun getFullSurah(@Path("number") surahNumber: Int): SurahFullResponse
}
