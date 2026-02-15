package com.example.sncf

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SncfService {
    @GET("coverage/sncf/places")
    suspend fun getPlaces(
        @Header("Authorization") auth: String,
        @Query("q") query: String
    ): NavitiaResponse

    @GET("coverage/sncf/journeys")
    suspend fun getJourneys(
        @Header("Authorization") auth: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("datetime") datetime: String,
        @Query("min_nb_journeys") nb: Int = 5
    ): JourneyResponse
}