// In: data/network/AladhanApiService.kt
package com.seif.salatukalyawm.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AladhanApiService {
    @GET("v1/timings/{date}" )
    suspend fun getPrayerTimes(
        @Path("date") date: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 5
    ): PrayerTimesResponse
}
