package com.example.flightsearch.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.flightsearch.data.FlightDatabase
import com.example.flightsearch.data.entity.AirportEntity
import com.example.flightsearch.data.entity.FavoriteEntity
import com.example.flightsearch.datastore.SearchPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FlightViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FlightDatabase.getDatabase(application)
    private val airportDao = db.airportDao()
    private val favoriteDao = db.favoriteDao()

    // LiveData: предложения по вводу
    private val _searchResults = MutableLiveData<List<AirportEntity>>()
    val searchResults: LiveData<List<AirportEntity>> = _searchResults

    // LiveData: маршруты из выбранного аэропорта
    private val _flightsFromAirport = MutableLiveData<List<Pair<AirportEntity, AirportEntity>>>()
    val flightsFromAirport: LiveData<List<Pair<AirportEntity, AirportEntity>>> = _flightsFromAirport

    // LiveData: избранные маршруты
    private val _favorites = MutableLiveData<List<FavoriteEntity>>()
    val favorites: LiveData<List<FavoriteEntity>> = _favorites

    // --- Работа с поиском ---

    fun searchAirports(query: String) = viewModelScope.launch {
        val results = airportDao.searchAirports("%$query%")
        _searchResults.postValue(results)
    }

    fun loadSavedQuery(context: Context): Flow<String> {
        return SearchPreferences.getSearchQuery(context)
    }

    fun saveQuery(context: Context, query: String) = viewModelScope.launch {
        SearchPreferences.saveSearchQuery(context, query)
    }

    // --- Маршруты из аэропорта ---

    fun getFlightsFrom(departureCode: String) = viewModelScope.launch {
        val allAirports = airportDao.getPopularAirports()
        val routes = allAirports.filter { it.iataCode != departureCode }
            .map { destination ->
                Pair(airportDao.getAirportByCode(departureCode), destination)
            }
        _flightsFromAirport.postValue(routes)
    }

    // --- Работа с избранным ---

    fun loadFavorites() = viewModelScope.launch {
        _favorites.postValue(favoriteDao.getFavorites())
    }

    fun isFavorite(departure: String, destination: String, callback: (Boolean) -> Unit) = viewModelScope.launch {
        val exists = favoriteDao.isFavorite(departure, destination) > 0
        callback(exists)
    }

    fun toggleFavorite(departure: String, destination: String) = viewModelScope.launch {
        val exists = favoriteDao.isFavorite(departure, destination) > 0
        if (exists) {
            favoriteDao.deleteFavorite(departure, destination)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(departureCode = departure, destinationCode = destination))
        }
        loadFavorites() // обновить
    }

    // загружает все аэропорты один раз и сохраняет их в searchResults, чтобы MainActivity могла использовать их для сопоставления с избранными
    fun preloadAirports() = viewModelScope.launch {
        val allAirports = airportDao.getPopularAirports()
        _searchResults.postValue(allAirports)
    }
}
