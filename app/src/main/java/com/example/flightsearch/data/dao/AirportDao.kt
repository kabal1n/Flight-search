package com.example.flightsearch.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.data.entity.AirportEntity

@Dao
interface AirportDao {

    @Query("SELECT * FROM airport WHERE iata_code LIKE :query OR name LIKE :query ORDER BY passengers DESC")
    suspend fun searchAirports(query: String): List<AirportEntity>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    suspend fun getAirportByCode(iataCode: String): AirportEntity

    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    suspend fun getPopularAirports(): List<AirportEntity>
}
