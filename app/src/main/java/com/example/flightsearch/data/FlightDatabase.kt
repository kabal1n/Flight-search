package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flightsearch.data.dao.AirportDao
import com.example.flightsearch.data.dao.FavoriteDao
import com.example.flightsearch.data.entity.AirportEntity
import com.example.flightsearch.data.entity.FavoriteEntity

@Database(entities = [AirportEntity::class, FavoriteEntity::class], version = 1)
abstract class FlightDatabase : RoomDatabase() {

    abstract fun airportDao(): AirportDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile private var INSTANCE: FlightDatabase? = null

        fun getDatabase(context: Context): FlightDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlightDatabase::class.java,
                    "flight_search.db"
                )
                    .createFromAsset("databases/flight_search.db") // ✅ копия из assets
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
