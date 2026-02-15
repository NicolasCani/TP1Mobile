package com.example.sncf

data class NavitiaResponse(
    val places: List<Place>
)

data class Place(
    val id: String,
    val name: String
)

data class JourneyResponse(
    val journeys: List<Journey>?
)

data class Journey(
    val duration: Long,
    val departure_date_time: String,
    val arrival_date_time: String
)