package com.example.flightsearch.data.dao

import androidx.room.*
import com.example.flightsearch.data.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorite")
    suspend fun getFavorites(): List<FavoriteEntity>

    @Query("DELETE FROM favorite WHERE departure_code = :departure AND destination_code = :destination")
    suspend fun deleteFavorite(departure: String, destination: String)

    @Query("SELECT COUNT(*) FROM favorite WHERE departure_code = :departure AND destination_code = :destination")
    suspend fun isFavorite(departure: String, destination: String): Int
}
